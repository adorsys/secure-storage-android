PROJECT_VERSION="$(./gradlew -q pV)"

    # Is this not a build which was triggered by setting a new tag?
    if [ -z "$TRAVIS_TAG" ]; then
      echo -e "Starting to tag commit.\n"

      git config --global user.email "travis@travis-ci.org"
      git config --global user.name "Travis"

      git tag -a v${PROJECT_VERSION} -m "Travis build $PROJECT_VERSION pushed a tag."
      git push origin --tags
      git fetch origin
      echo -e $PROJECT_VERSION
      echo -e "Done magic with tags.\n"

            echo -e "Start clean module"
      ./gradlew :securestorage:clean
      echo -e "Finished clean"

      echo -e "Start install module"
      ./gradlew :securestorage:install
      echo -e "Finished install"

      echo -e "Start bintrayUpload"
      ./gradlew :securestorage:bintrayUpload
      echo -e "Finished bintrayUpload"
  fi