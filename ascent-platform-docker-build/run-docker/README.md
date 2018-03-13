# Run-Docker
## Local Developer Usage
#### To run...
- cd into the run-docker directory (`cd run-docker`)
- run the program (`./run-docker`), which calls run-dockers help menu, showing you how to use the program

##### Troubleshooting
- exec format issues
<br />If you're having exec format issues with the run-docker binary, you will need to have go rebuild according to your architecture. Just Run
<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`env GOOS=[YOUR_OS_HERE] GOARCH=[YOUR_ARCH_TYPE_HERE] go build`
<br />See https://golang.org/doc/install/source#environment for values that $GOOS and $GOARCH can support.
<br /><br />NOTE: This does *NOT* necessarily mean that you can run this directly on your windows computer, as run-docker uses docker-compose and docker commands that may not be supported by windows. It hasn't been tested directly on windows. See https://github.com/department-of-veterans-affairs/ascent-developer-vm/blob/master/README.md for getting your vm set up first. 
