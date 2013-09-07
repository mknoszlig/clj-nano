# What is clj-nano?

Clojure bindings for the [nanomsg][1] messaging library.
A thin wrapper around the [jnano][2] library.

[1]:http://nanomsg.org
[2]:https://github.com/gonzus/jnano


## Installing

1. Install libnanomsg. (see https://github.com/nanomsg/nanomsg)
2. Install jnano (https://github.com/gonzus/jnano)
3. add jnano-0.1 jar to local maven repo:
   
   ```shell
   mvn install:install-file -Dfile=build/java/jar/jnano-0.1.jar -DgroupId=nanomsg \
    -DartifactId=jnano -Dversion=0.1 -Dpackaging=jar
   ```
4. edit project.clj :jvm-opts to include the location of the jnano
   c lib (/path/to/jnano/build/c/sharedlibrary).


## Usage

Please refer to the performance tests underneath `src/clj-nano/perf/` for now.

## Known Limitations

- no variable message size. (no jnano support)
- only supports setting of int socket options. (no jnano support)

## License

Copyright © 2013 Maximilian Karasz

Distributed under the MIT/X11 License, same as nanomsg.
