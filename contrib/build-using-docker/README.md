
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

