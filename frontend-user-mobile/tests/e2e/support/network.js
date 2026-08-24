export async function blockExternalImages(page) {
  await page.route('**/*', (route) => {
    const request = route.request()
    if (request.resourceType() !== 'image') return route.continue()
    const host = new URL(request.url()).hostname
    if (host === 'localhost' || host === '127.0.0.1') return route.continue()
    return route.abort('blockedbyclient')
  })
}
