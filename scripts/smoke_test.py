#!/usr/bin/env python3
"""E2E smoke test for the E-role backend. Runs the full user journey over HTTP."""
import json
import sys
import urllib.request
import urllib.error

BASE = "http://localhost:18080"


results = []


def call(method, path, body=None, headers=None):
    url = BASE + path
    data = json.dumps(body).encode("utf-8") if body is not None else None
    h = {"Content-Type": "application/json"}
    if headers:
        h.update(headers)
    req = urllib.request.Request(url, data=data, method=method, headers=h)
    with urllib.request.urlopen(req, timeout=15) as resp:
        raw = resp.read().decode("utf-8")
        return json.loads(raw)


def ok(name, cond, detail=""):
    print(("PASS" if cond else "FAIL"), name, detail)
    return (name, bool(cond), detail)


def main():
    # 1. Public banners
    r = call("GET", "/api/banners")
    results.append(ok("GET /api/banners returns 4", r["code"] == "SUCCESS" and len(r["data"]) == 4,
                      f"({len(r['data'])} banners)"))

    # 2. Cart before
    r = call("GET", "/api/cart", headers={"X-User-Id": "4"})
    before_ids = [i["id"] for i in r["data"]["items"]]
    results.append(ok("GET /api/cart has 3 items", len(before_ids) == 3, f"({before_ids})"))

    # 3. Create order (cart items 1 & 2 belong to merchant 1 & 2)
    r = call("POST", "/api/orders", {"addressId": 1, "cartItemIds": [1, 2], "remark": "smoke"},
             {"X-User-Id": "4"})
    results.append(ok("POST /api/orders succeeds", r["code"] == "SUCCESS" and r["data"]["id"] is not None))
    oid = r["data"]["id"]
    results.append(ok("order merchant is merchant 1", r["data"]["merchantId"] == 1,
                      f"(merchant={r['data']['merchantId']})"))
    results.append(ok("order status PENDING_PAYMENT", r["data"]["status"] == 0))

    # 4. Order split -> second order for merchant 2 appears in list
    r = call("GET", "/api/orders?size=20", headers={"X-User-Id": "4"})
    merchant_ids = sorted({o["merchantId"] for o in r["data"]["records"]})
    results.append(ok("orders split across merchants", 1 in merchant_ids and 2 in merchant_ids,
                      f"(merchants={merchant_ids})"))

    # 5. Pay order
    r = call("POST", f"/api/orders/{oid}/pay", None, {"X-User-Id": "4"})
    results.append(ok("POST pay succeeds", r["code"] == "SUCCESS" and r["data"]["paymentNo"] is not None,
                      f"(paymentNo={r['data']['paymentNo'] if r['data'] else None})"))

    # 6. Merchant ships
    r = call("PUT", f"/api/merchant/orders/{oid}/ship", {"logisticsCompany": "顺丰速运", "logisticsNo": "SF8888888"},
             {"X-Merchant-Id": "1"})
    results.append(ok("merchant ships order", r["code"] == "SUCCESS" and r["data"]["status"] == 2,
                      f"(status={r['data']['status']})"))

    # 7. Merchant dashboard reflects pending shipments
    r = call("GET", "/api/merchant/dashboard", headers={"X-Merchant-Id": "1"})
    results.append(ok("merchant dashboard ok", r["code"] == "SUCCESS", f"({r['data']})"))

    # 8. User confirms receipt
    r = call("PUT", f"/api/orders/{oid}/confirm-receipt", None, {"X-User-Id": "4"})
    results.append(ok("confirm receipt -> RECEIVED", r["code"] == "SUCCESS" and r["data"]["status"] == 3))

    # 9. User reviews product
    r = call("POST", "/api/reviews", {"orderId": oid, "productId": 1, "content": "很好", "rating": 5, "anonymous": False},
             {"X-User-Id": "4"})
    results.append(ok("create review", r["code"] == "SUCCESS" and r["data"]["id"] is not None))
    review_id = r["data"]["id"]

    # 10. Merchant replies to review
    r = call("PUT", f"/api/merchant/reviews/{review_id}/reply", {"reply": "谢谢支持"}, {"X-Merchant-Id": "1"})
    results.append(ok("merchant replies review", r["code"] == "SUCCESS" and r["data"]["merchantReply"] == "谢谢支持"))

    # 11. User applies refund on order 2 (PENDING_SHIPMENT, merchant 1, user 4)
    r = call("POST", "/api/refunds", {"orderId": 2, "reason": "不想要了", "description": "test", "amount": 129.00},
             {"X-User-Id": "4"})
    results.append(ok("create refund", r["code"] == "SUCCESS" and r["data"]["id"] is not None))
    rid = r["data"]["id"]

    # 12. Merchant approves refund
    r = call("PUT", f"/api/merchant/refunds/{rid}/audit", {"approved": True, "remark": "同意"}, {"X-Merchant-Id": "1"})
    results.append(ok("merchant approves refund", r["code"] == "SUCCESS" and r["data"]["status"] == 1))

    # 13. User submits return logistics
    r = call("PUT", f"/api/refunds/{rid}/return-logistics",
             {"logisticsCompany": "中通快递", "logisticsNo": "ZT12345"}, {"X-User-Id": "4"})
    results.append(ok("submit return logistics", r["code"] == "SUCCESS" and r["data"]["status"] == 2))

    # 14. Merchant confirms return -> completed
    r = call("PUT", f"/api/merchant/refunds/{rid}/confirm-return", None, {"X-Merchant-Id": "1"})
    results.append(ok("confirm return -> COMPLETED", r["code"] == "SUCCESS" and r["data"]["status"] == 3))

    # 15. Admin refund arbitration list and a fresh arbitrate
    r = call("POST", "/api/refunds", {"orderId": 6, "reason": "性能不达标", "description": "again", "amount": 4299.00},
             {"X-User-Id": "5"})
    rid2 = r["data"]["id"]
    call("PUT", f"/api/merchant/refunds/{rid2}/audit", {"approved": False, "remark": "拒绝"}, {"X-Merchant-Id": "1"})
    call("PUT", f"/api/refunds/{rid2}/appeal", {"reason": "申请介入"}, {"X-User-Id": "5"})
    r = call("GET", "/api/admin/refunds", headers={"X-Admin-Id": "1"})
    results.append(ok("admin sees appealed refund", r["code"] == "SUCCESS" and any(x["id"] == rid2 for x in r["data"]["records"])))
    r = call("PUT", f"/api/admin/refunds/{rid2}/arbitrate", {"supportUser": True, "remark": "支持用户"},
             {"X-Admin-Id": "1"})
    results.append(ok("admin arbitrates refund", r["code"] == "SUCCESS" and r["data"]["status"] == 6))

    # 16. Admin dashboard & logs
    r = call("GET", "/api/admin/dashboard", headers={"X-Admin-Id": "1"})
    results.append(ok("admin dashboard", r["code"] == "SUCCESS" and r["data"]["orderCount"] >= 6))
    r = call("GET", "/api/admin/logs?size=10", headers={"X-Admin-Id": "1"})
    results.append(ok("admin logs have REFUND_ARBITRATE", any(l["action"] == "REFUND_ARBITRATE" for l in r["data"]["records"])))

    # 17. Banner CRUD
    r = call("POST", "/api/admin/banners", {"title": "新轮播", "imageUrl": "http://img/x.png", "linkUrl": "/x", "sort": 99, "enabled": True},
             {"X-Admin-Id": "1"})
    bid = r["data"]["id"]
    results.append(ok("admin creates banner", r["code"] == "SUCCESS" and bid is not None))
    r = call("PUT", f"/api/admin/banners/{bid}", {"title": "改标题", "imageUrl": "http://img/y.png", "linkUrl": "/y", "sort": 98, "enabled": False},
             {"X-Admin-Id": "1"})
    results.append(ok("admin updates banner", r["code"] == "SUCCESS" and r["data"]["title"] == "改标题"))
    r = call("DELETE", f"/api/admin/banners/{bid}", None, {"X-Admin-Id": "1"})
    results.append(ok("admin deletes banner", r["code"] == "SUCCESS"))

    passed = sum(1 for _, c, _ in results if c)
    total = len(results)
    print(f"\n=== {passed}/{total} checks passed ===")
    failed = [n for n, c, _ in results if not c]
    if failed:
        print("FAILED:", failed)
        sys.exit(1)


if __name__ == "__main__":
    main()
