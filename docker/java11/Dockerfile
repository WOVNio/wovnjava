FROM tomcat:10-jdk11
RUN apk update && \
    apk add --no-cache bash curl && \
    apk add openrc --no-cache && \
    apk add openssh-server && \
    rc-update add sshd && \
    rc-status && \
    touch /run/openrc/softlevel

WORKDIR /usr/local/tomcat/webapps

ADD hello/target ./
ADD scripts/startup.sh .

CMD ["sh" , "startup.sh"]