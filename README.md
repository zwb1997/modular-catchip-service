# mutiple-modules
mutiple modules test for threads web tasks

[use]:
  springboot<br>
  muti-thread<br>
  httpclient<br>
  jsoup<br>
  logback<br>
  druid<br>
  mysql<br>
Plan:
  1.Use muti-threads to work <strong>done</strong> <br> 
  2.break up data fetching module and data storage module <strong>working</strong> <br>  
  3.Try to use cache(like redis..memecached..) <strong>working</strong> <br>
  4.How to efficient read and write datas..<br>
  etc...<br>
  ////////////////////
record some parameters</br>
"120.84.100.203:9999" ip + port </br>
"HTTP代理" proxy support </br>
"普通代理" proxy type </br>
"中国" location</br>
"广东"location</br>
"揭阳"location</br>
"联通"location</br>
"3.17" speed</br>
"384天" alive time</br>
"18小时" alive time</br>
"20分钟" alive time</br>
"58秒" alive time</br>
"274" web set score


list must be 10 elements; <br>
固定格式的集合;<br> 
0 -> ip ;<br>
1 -> port ;<br>
2 -> ip地址 ; <br>
3 -> ip供应商 ; <br>
4 -> 是否支持https ;<br> 
5 -> 是否支持post请求 ; <br>
6 -> 匿名程度 ; <br>
7 -> 速度; <br>
8 -> 网站检测ip入库时间 ; <br>
9 -> 网站检测 ip最后有效时间; 具体请看 {@link IpPoolMainDTO }<br>
