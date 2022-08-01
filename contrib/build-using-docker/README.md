
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
could host the app from there. But be aware, that hardware (like a scanner or
webcams) most probably will not work when sentinel gets started within a docker
container. So to use sentinel, it usually makes more sense to install it to the
host machine. If we still want to use docker, we would need to configure the
docker container that the app will have access to the hardware.

First we setup the directory on the host side where sentinel will store its
data and log files.

```sh
sudo mkdir -p /var/opt/sentinel/work
sudo chown 1000:1000 /var/opt/sentinel/work
```

Run Sentinel Server:
```sh
sudo docker run --name sentinel-container --rm -ti -p 127.0.0.1:8080:8080 -e DISPLAY -v "/tmp/.X11-unix:/tmp/.X11-unix:ro" -v "/var/opt/sentinel/work:/work:rw" "${IMG:?}" sh -c sentinel-server
```

Run Sentinel Client:
```sh
sudo docker exec -ti -e DISPLAY sentinel-container sh -c sentinel-client
```


