FROM amazoncorretto:22-alpine-jdk

# on Ubuntu it's actually (also, it comes with a single default font dejavu, so didn't have to install it explicitly):
# RUN apt update && apt install -y fontconfig libfreetype6 && apt upgrade -y
#
# Since we generate Structure SVGs in Java, and those structures often need to depict text (the names of the elements),
# we need to deal with the fonts on BE:
#  - fontconfig is an API through which linux utilities usually load & work with fonts
#  - freetype is an actual implementation that can read & process freetype (vector-based) fonts. It's used by fontconfig.
#  - dejavu is just one of the possibilities. If we ever need to change that, note that CDK loads SansSerif font, so
#    we can't pick just any.
RUN apk update && apk add fontconfig freetype font-dejavu && apk upgrade
COPY entrypoint.sh cdk-rest-template.jar /opt/

ENTRYPOINT ["/bin/sh", "/opt/entrypoint.sh"]