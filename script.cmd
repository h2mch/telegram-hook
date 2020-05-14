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


# Shortcut for clouddeployment
λ docker build . --tag  gcr.io/quarkus-265809/webhook
λ docker push gcr.io/quarkus-265809/webhook
λ gcloud run deploy webhook --image gcr.io/quarkus-265809/webhook --platform managed --region europe-west1 --allow-unauthenticated


# Start Jaeger Server (gRPC Port: 114250, WebUI: 16686)
λ docker run    -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp \
                -p 5778:5778 -p 16686:16686 \
                -p 14268:14268 -p 14250:14250 \
                jaegertracing/all-in-one:latest

# Start Jaeger Server
λ java -jar -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
          -Dtelegram.token=abcd:123 \
          -javaagent:lib/opentelemetry-auto-0.2.2.jar \
          -Dota.exporter.jar=lib/opentelemetry-auto-exporters-jaeger-0.2.2.jar \
          -Dota.exporter.jaeger.endpoint=localhost:14250 \
          -Dota.exporter.jaeger.service.name=telegram-hook \
          target\telegram-hook-1.0.0-SNAPSHOT-runner.jar