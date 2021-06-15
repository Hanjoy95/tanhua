# 启动redis
docker run -p 6379:6379 ^
  --name redis ^
  -v C:/Users/Hanjoy/.docker/redis/data:/data ^
  -v C:/Users/Hanjoy/.docker/redis/redis.conf:/etc/redis/redis.conf ^
  -d redis redis-server /etc/redis/redis.conf

# 启动mysql
docker run -p 3306:3306 ^
  --name mysql ^
  -v C:/Users/Hanjoy/.docker/mysql/data:/var/lib/mysql ^
  -e MYSQL_ROOT_PASSWORD=123456 ^
  -d mysql

# 启动rabbitmq
docker run -p 15672:15672 -p 5672:5672 ^
  --name rabbitmq ^
  --hostname my-rabbitmq ^
  -d rabbitmq:management

# 启动mongodb
docker run -p 27017:27017 ^
  --name mongodb ^
  -v C:/Users/Hanjoy/.docker/mongodb/data:/data/db ^
  -d mongo

# 启动zookeeper
docker run -p 2181:2181 ^
  --name zookeeper ^
  -v C:/Users/Hanjoy/.docker/zookeeper/data:/data ^
  -d zookeeper

# 启动dubbo
docker run -p 8080:8080 ^
  --name dubbo ^
  -e dubbo.registry.address=zookeeper://127.0.0.1:2181 ^
  -e dubbo.admin.root.password=root ^
  -e dubbo.admin.guest.password=guest ^
  -d chenchuxin/dubbo-admin
  
docker run -p 8080:8080 ^
  --name dubbo ^
  -d apache/dubbo-admin