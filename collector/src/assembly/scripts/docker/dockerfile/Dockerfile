FROM java:8-alpine



ENV APP_RUN /var/run/app

ENV HTTP_PORT 8081

ENV DEBUG_PORT 8777

RUN mkdir -p ${APP_RUN}

COPY smartsight-dbmonitor-collector ${APP_RUN}

COPY smartsight-dbmonitor-agent ${APP_RUN}/agent

RUN chmod 755 ${APP_RUN}/*.sh

RUN chmod 755 ${APP_RUN}/agent/*.sh

EXPOSE ${HTTP_PORT} ${DEBUG_PORT}

CMD ${APP_RUN}/run.sh
