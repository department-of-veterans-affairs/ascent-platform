# Run-Docker
## Local Developer Usage
#### To run...
- cd into the run-docker directory (`cd run-docker`)
- run the program (`./run-docker`), which calls run-dockers help menu, showing you how to use the program

#### Some use cases
- Centralized logging:
``` bash
./run-docker start logging                            # brings up the logging cluster
./run-docker start logging --build                    # Build your local changes and start container from that.
./run-docker stop logging                             # stops the logging cluster
./run-docker stop logging --clean                     # removes images and volumes for logging stack
./run-docker start logging --container elasticsearch  # starts only the elasticsearch container in logging
./run-docker stop logging --container elasticsearch   # stops only the elasticsearch container
```

- Start all platform containers and supporting services:

``` bash
ascent-platform/ascent-builds.sh                       # get all of the supporting servies for platform
cd ascent-platform-docker-build/run-docker
./run-docker start all                                 # start up all containers ... you can also use --container to bring up only one of them, but there is no support for bringing up one of the supporting platform service containers (e.g, can't do --container ascent-gateway yet)
./run-docker stop all                                  # stop all containers
```
- There are also localint, logging, and sonarqube profiles to start and stop. See `./run-docker` and it will print out a help menu. `./run-docker start --help` for help menu for the start command, and `./run-docker start all --help` for extra options, also

##### Troubleshooting
- E: You don't have enough free space in /var/cache/apt/archives/.

You'll have this issue if you have too many images/containers store in your cache for docker. To fix, run the following:
```
 docker rmi $(docker images -q)
 docker rm -v $(docker ps -qa)
```

- exec format issues
<br />If you're having exec format issues with the run-docker binary, you will need to have go rebuild according to your architecture. Just Run
<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`env GOOS=[YOUR_OS_HERE] GOARCH=[YOUR_ARCH_TYPE_HERE] go build`
<br />See https://golang.org/doc/install/source#environment for values that $GOOS and $GOARCH can support.
<br /><br />NOTE: This does *NOT* necessarily mean that you can run this directly on your windows computer, as run-docker uses docker-compose and docker commands that may not be supported by windows. It hasn't been tested directly on windows. See https://github.com/department-of-veterans-affairs/ascent-developer-vm/blob/master/README.md for getting your vm set up first.
