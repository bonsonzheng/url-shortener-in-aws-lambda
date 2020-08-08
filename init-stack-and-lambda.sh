#!/bin/bash
set -eo pipefail
mvn package
aws cloudformation package --template-file template_mvn.yml --s3-bucket lambda-artifacts-url-shortener-seoul --output-template-file outputtemplate.yml
aws cloudformation deploy --template-file outputtemplate.yml --stack-name url-shortener-stack --capabilities CAPABILITY_NAMED_IAM
