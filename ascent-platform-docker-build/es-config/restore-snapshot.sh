#!/bin/bash
#
# Restore a snapshot from our repository

set -e

function print_usage {
  echo
  echo "Usage: restore-snapshot [OPTIONS]"
  echo
  echo "Execute the Elasticsearch restore process for all indecies."
  echo
  echo "Options:"
  echo
  echo -e "  --username\t\tThe username to authenticate with Elasticsearch. This user must be an administrator. Required."
  echo -e "  --password\t\tThe password to authenticate with Elasticsearch. Required."
  echo -e "  --snapshot-id\t\tThe Snapshot ID to restore. If not specified, a list of all available snapshot ids will be returned."
  echo
  echo "Example:"
  echo
  echo "  restore-snapshot --username admin --password securepass"
}

function es_url {
    cat "es-url"
}

function assert_not_empty {
  local readonly arg_name="$1"
  local readonly arg_value="$2"

  if [[ -z "$arg_value" ]]; then
    echo "The value for '$arg_name' cannot be empty"
    print_usage
    exit 1
  fi
}

function restore_snapshot {
    local USER=$1
    local PASSWORD=$2
    local SNAPSHOT=$3

    # We need to close the indices first
    echo 'Closing all indecies...'
    curl -XPOST -s -u $USER:$PASSWORD $(es_url)/_all/_close

    # Restore the snapshot we want
    echo "Staring restoration of snapshot $SNAPSHOT..."
    curl -XPOST -s -u $USER:$PASSWORD $(es_url)/_snapshot/s3_repository/$SNAPSHOT/_restore -d '{
    "ignore_unavailable": true
    }'

    # Restore process automatically re-opens restored indices
    # Re-open the indices
    #curl -XPOST $(es_url)/_all/_open
    echo "Restoration of snapshot $SNAPSHOT complete!"
}

function list_snapshots {
    local USER=$1
    local PASSWORD=$2
    curl -XGET -s -u $USER:$PASSWORD $(es_url)/_snapshot/s3_repository/_all?verbose=false | jq '.'
}

function run {
  local username=""
  local password=""
  local snapshot_id=""
  local all_args=()

  while [[ $# > 0 ]]; do
    local key="$1"

    case "$key" in
      --username)
        assert_not_empty "$key" "$2"
        username="$2"
        shift
        ;;
      --password)
        assert_not_empty "$key" "$2"
        password="$2"
        shift
        ;;
      --snapshot-id)
        assert_not_empty "$key" "$2"
        snapshot_id="$2"
        shift
        ;;
      --help)
        print_usage
        exit
        ;;
      *)
        echo "Unrecognized argument: $key"
        print_usage
        exit 1
        ;;
    esac

    shift
  done

  if [[ -z "$username" ]]; then
    log_error "--username parameter must be set."
    exit 1
  fi

  if [[ -z "$password" ]]; then
    log_error "--password parameter must be set."
    exit 1
  fi

  if [[ "$snapshot_id" ]]; then
    #Restore that snapshot4
    restore_snapshot $username $password $snapshot_id
  else
    #List all snapshots
    echo 'No snapshot ID specified, you must specify one of the following snapshot IDs:'
    list_snapshots $username $password
  fi
}

run "$@"