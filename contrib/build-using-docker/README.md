
Showcase how to build
=====================

## Setup variable to reduce annoying repetitions

```sh
IMG=mil-sentinel-build:latest
```


## Make and install dockerimage

```sh
curl -sSL https://github.com/hiddenalpha/sentinel/raw/master/contrib/build-using-docker/Dockerfile | sudo docker build . -f - -t "${IMG:?}"
```


## Grab distribution archives

Most probably we wanna get the distribution archive. We can copy it out the
dockerimage to our current working dir using:

```sh
sudo docker run --rm -i "${IMG:?}" sh -c 'true && cd dist && tar c *.tgz' | tar x
```


## Explore created image

Just in case we need to dig around in the built image.

```sh
sudo docker run --rm -ti "${IMG:?}" sh
```


## Host from image

As a goodie the app also gets installed inside the dockerfile. So in theory we
could host the app from there.

WARN: This example created temporary instances! For production use make sure
you mount the required data directories from a persistent storage to prevent
data loss over restarts.

Server:
```sh
sudo mkdir -p /var/opt/sentinel/work
sudo chown 1000:1000 /var/opt/sentinel/work
sudo docker run --name sentinel-container --rm -ti -p 127.0.0.1:8080:8080 -e DISPLAY -v "/tmp/.X11-unix:/tmp/.X11-unix:ro" -v "/var/opt/sentinel/work:/work:rw" "${IMG:?}" sh -c sentinel-server
```

Client:
```sh
sudo docker exec -ti -e DISPLAY sentinel-container sh -c sentinel-client
```


