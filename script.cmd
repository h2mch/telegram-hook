C:\source\graalvm\telegram-hook

# Build and deploy
λ docker build -t h2mch/webhook .
λ docker tag h2mch/webhook:latest gcr.io/quarkus-265809/webhook
λ docker push gcr.io/quarkus-265809/webhook

#Test
λ docker run --rm -it -p 8080:8080 h2mch/clouddeployment



λ gcloud container images delete gcr.io/quarkus-265809/webhook --force-delete-tags

gcloud container images list-tags gcr.io/quarkus-265809/webhook --filter='-tags:*'  --format='get(digest)' --limit=$BIG_NUMBE

# Register a new webhook

λ $token = 'your-bot-token'
λ $url = 'your-cloud-function-endpoint'
λ Invoke-WebRequest https://api.telegram.org/bot$token/setWebhook?url=$url
