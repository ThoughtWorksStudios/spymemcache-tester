Instructions
============

gradle distZip

copy it to your ec2 instance that has the right security group to access your elasticache cluster

export MEMCACHE_ENDPOINTS=your-cluster.abcd.0001.usw1.cache.amazonaws.com,your-cluster.abcd.0002.usw1.cache.amazonaws.com

set it to a comma-seaprated list of endpoints

in the extracted zip, go to bin and run it

by default it has 2 writer threads and 5 reader threads

wait awhile

hit ctrl+c to stop and see summary

bill dephillips
