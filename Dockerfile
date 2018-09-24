FROM anapsix/alpine-java:8_server-jre

WORKDIR /opt/respondeco

COPY target/universal/*.zip respondeco.zip
RUN unzip respondeco.zip
RUN mv respondeco-*/* .
RUN rm -rf respondeco.zip respondeco-*
RUN ls -als
RUN pwd

EXPOSE 9000

CMD bin/respondeco
