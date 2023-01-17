#!/bin/sh

mirror=$(curl --stderr /dev/null https://www.apache.org/dyn/closer.cgi?as_json=1 | jq -r '.preferred')
echo "mirror=$mirror"
url="https://dlcdn.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz"
echo "URL=${url}"
wget -q "${url}" -O "/tmp/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz"
