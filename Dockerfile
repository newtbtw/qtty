FROM ubuntu:latest
LABEL authors="newt"

ENTRYPOINT ["top", "-b"]