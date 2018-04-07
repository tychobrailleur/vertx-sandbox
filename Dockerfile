FROM tychobrailleur/java8

RUN apt-get install postgresql

# Create specific user
RUN useradd --create-home --shell /bin/bash processit && adduser processit sudo
RUN echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

RUN mkdir -p /var/data/processit && chown -R processit:processit /var/data/processit


USER processit
WORKDIR /home/processit

COPY --chown=processit ./target/vertx-sandbox.jar /home/processit

CMD nohup java -jar vertx-sandbox.jar & bash
