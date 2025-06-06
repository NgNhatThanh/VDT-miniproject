sleep 15
kafka-topics --create --topic new-meeting-history --bootstrap-server kafka:9092 --replication-factor 1 --partitions 3
kafka-topics --create --topic meeting_leave --bootstrap-server kafka:9092 --replication-factor 1 --partitions 3