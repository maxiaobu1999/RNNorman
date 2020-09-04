#视频缓存 videocache模块说明
##基于https://github.com/danikula/AndroidVideoCache 2.7.1版本

##需求
Q：不支持预加载 
A：写个请求调用缓存URL，避免与videoView绑定 参考https://github.com/fightingBirdCaiy/JmVideoCache/commit/e06674b48fbd78c98e12572cdb16da691f87e0e2
Q：不支持取消特定URL
A：写个方法
 