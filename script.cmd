C:\source\graalvm\telegram-hook

# Build and deploy
λ docker build -t h2mch/webhook .
λ docker tag h2mch/webhook:latest gcr.io/quarkus-265809/webhook
λ docker push gcr.io/quarkus-265809/

#Test
λ docker run --rm -it -p 8080:8080 h2mch/clouddeployment
