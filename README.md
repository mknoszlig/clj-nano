# clj-nano

Clojure bindings for the nanomsg messaging library[1].
A thin wrapper around the jnano[2] library.

[1] http://nanomsg.org
[2] https://github.com/gonzus/jnano


## Installing

1. Install libnanomsg. (see https://github.com/nanomsg/nanomsg)
2. Install jnano (https://github.com/gonzus/jnano)
3. add jnano-0.1 jar to local maven repo:
   mvn install:install-file -Dfile=build/java/jar/jnano-0.1.jar\
   -DgroupId=nanomsg -DartifactId=jnano -Dversion=0.1 -Dpackaging=jar
4. edit project.clj :jvm-opts to include the location of the jnano
   c lib (/path/to/jnano/build/c/sharedlibrary).


## Usage

FIXME

## Known Limitations

- no variable message size atm, since it is not supported by jnano yet.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
