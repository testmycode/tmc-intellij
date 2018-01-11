mkdir -p target/dependency/intellij-idea
curl -L https://nygren.xyz/ideaIC-2017.1.1.tar.gz | tar xz --strip-components=1 -C target/dependency/intellij-idea
export DISPLAY=:0.0
