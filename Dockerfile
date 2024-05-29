# Pull base image
FROM registry.cn-zhangjiakou.aliyuncs.com/jwsem/adoptopenjdk:11-jre-hotspot
MAINTAINER alan "aijingyuan@qfei.cn"
#设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
COPY ./target/matrix-connect-0.0.1-SNAPSHOT.jar /usr/share
ENV SPRING_OUTPUT_ANSI_ENABLED ALWAYS
ENV JHIPSTER_SLEEP 0
ENV JAVA_OPTS ""
EXPOSE 21507
# Define default command.
