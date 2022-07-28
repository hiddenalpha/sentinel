
Showcase how to build and install
=================================


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
dockerimage to our host using:

```sh
sudo docker run --rm -i "${IMG:?}" sh -c 'true && cd dist && tar c *.tgz' | tar x
```


## Explore created image

```sh
sudo docker run --rm -ti "${IMG:?}" sh
```


## TODO remove those notes

mvn -pl client -am clean verify

