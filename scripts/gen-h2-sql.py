#!/usr/bin/env python3
"""Generate H2-compatible schema.sql and data.sql from the MySQL originals.

MySQL -> H2 (MODE=MySQL) transformations:
  1. Drop `CREATE DATABASE` / `USE` statements.
  2. Drop `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='...'` table options.
  3. Drop inline column `COMMENT '...'` clauses.
  4. Replace `DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`
     with `DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP` (H2 2.x rejects ON UPDATE in column def).
  5. TINYINT / BIGINT / DECIMAL etc. are natively understood by H2 in MySQL mode.
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DB = ROOT / "database"
OUT = ROOT / "backend" / "src" / "main" / "resources"


def transform_schema(text: str) -> str:
    # Remove the whole CREATE DATABASE block (incl. continuation lines until ';')
    text = re.sub(
        r"CREATE\s+DATABASE[^;]*;", "", text, flags=re.IGNORECASE | re.DOTALL)
    lines = text.splitlines()
    out = []
    for raw in lines:
        line = raw
        # Drop USE lines
        if re.match(r"^\s*USE\s+`", line, re.IGNORECASE):
            continue
        # Drop ENGINE=... table option suffix
        line = re.sub(
            r"\s*ENGINE\s*=\s*InnoDB[^,;]*(?:DEFAULT\s+CHARSET\s*=\s*\S+)?\s*COMMENT\s*=\s*'[^']*'\s*",
            "", line, flags=re.IGNORECASE)
        line = re.sub(r"\s*ENGINE\s*=\s*InnoDB[^;]*", "", line, flags=re.IGNORECASE)
        # Drop inline column COMMENT '...'
        line = re.sub(r"\s+COMMENT\s+'[^']*'", "", line, flags=re.IGNORECASE)
        # ON UPDATE CURRENT_TIMESTAMP in column default
        line = re.sub(
            r"\s+ON\s+UPDATE\s+CURRENT_TIMESTAMP", "", line, flags=re.IGNORECASE)
        # Drop secondary KEY index lines (H2 index names are global; the originals
        # collide across tables). PRIMARY KEY / UNIQUE KEY constraints are kept.
        if re.match(r"^\s*KEY\s+`", line, re.IGNORECASE):
            continue
        out.append(line)
    text = "\n".join(out)
    # Remove trailing commas left before a closing paren of a CREATE TABLE block.
    text = re.sub(r",(\s*\))", r"\1", text)
    return text + "\n"


def transform_data(text: str) -> str:
    lines = text.splitlines()
    out = []
    for raw in lines:
        line = raw
        if re.match(r"^\s*USE\s+`", line, re.IGNORECASE):
            continue
        # Keep backtick-quoted identifiers as-is: H2 in MySQL mode supports
        # backticks, and tables like `user` are reserved words without them.
        out.append(line)
    return "\n".join(out) + "\n"


def main() -> None:
    schema = (DB / "schema.sql").read_text(encoding="utf-8")
    data = (DB / "data.sql").read_text(encoding="utf-8")

    (OUT / "schema-h2.sql").write_text(transform_schema(schema), encoding="utf-8")
    (OUT / "data-h2.sql").write_text(transform_data(data), encoding="utf-8")
    print("Wrote backend/src/main/resources/schema-h2.sql and data-h2.sql")


if __name__ == "__main__":
    main()
