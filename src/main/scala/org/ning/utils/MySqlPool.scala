package org.ning.utils

import java.sql.Connection
import java.util.Properties

import com.alibaba.druid.pool.DruidDataSourceFactory
import javax.sql.DataSource

object MySqlPool {
  def apply(ip: String, port: String, db: String, userName: String, password: String): MySqlPool = {
    val url = s"jdbc:mysql://$ip:$port/$db?useUnicode=true&characterEncoding=UTF8"
    val properties = new Properties()
    properties.setProperty("url", url)
    properties.setProperty("username", userName)
    properties.setProperty("password", password)
    properties.setProperty("driverClassName", "com.mysql.jdbc.Driver")
    properties.setProperty("initialSize", Integer.toString(2))
    properties.setProperty("minIdle", Integer.toString(1))
    properties.setProperty("maxActive", Integer.toString(20))
    properties.setProperty("maxWait", "60000")
    properties.setProperty("timeBetweenEvictionRunsMillis", "60000")
    properties.setProperty("minEvictableIdleTimeMillis", "300000")
    properties.setProperty("validationQuery", "SELECT 1")
    properties.setProperty("testWhileIdle", true.toString)
    properties.setProperty("testOnBorrow", true.toString)
    properties.setProperty("testOnReturn", true.toString)
    properties.setProperty("poolPreparedStatements", false.toString)
    properties.setProperty("maxPoolPreparedStatementPerConnectionSize", "-1")
    new MySqlPool(properties)
  }

}

class MySqlPool(properties: Properties) {
  val dataSource: DataSource = DruidDataSourceFactory.createDataSource(properties)
  def getConnection: Connection = this.dataSource.getConnection
}
