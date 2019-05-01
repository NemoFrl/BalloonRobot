# BalloonRobot
  这是一个能够快捷部署、开启、管理饥荒联机服务器的机器人。
你可以使用官方的气球仔机器人（加入qq群1020876285），也可以使用开源代码自己部署一个机器人。如果感兴趣的话就加入我吧，我们一起实现更多有趣的功能。</br>

注意事项：</br>
1.气球仔只能管理Linux中且为steam的饥荒服务器</br>

使用指南：</br>

一、使用官方气球仔</br>
1.加入q群</br>
2.私聊气球仔</br>
3.发送登录服务器命令</br>
4.发送安排好的命令给气球仔（相关命令在下文给出）</br>

二、自己部署气球仔</br>
1.在release中下载最新版本的气球仔。</br>
2.安装酷q，安装免费版cool air即可，https://cqp.cc/t/23253，</br>
3.在解压包中解压org.ruiko.lemoc.cpk插件到coolair安装目录下的app目录中</br>
4.启动酷air，设置lemoc插件，设置自启，并启动服务器。</br>
5.解压jar包和lib文件夹和start.bat到同一目录</br>
6.安装java，https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html</br>
7.双击start.bat看到connect success即可。</br>
8.这样一来你就拥有自己的气球仔啦，接下来和一中的3、4两点相同</br>

三、气球仔命令一览（#content 为输入参数）</br>
login 登录命令，具体输入login-help查看登录配置</br>
logout 注销命令，换Linux服务器时必须先注销</br>
dst #content 饥荒服务器后台命令，如dst c_announce(\"蛇皮\")，注意双引号要转义</br>
dst-initsteamcmd 安装steamcmd</br>
dst-update 更新饥荒服务器</br>
dst-restartmaster 重启地上服务器</br>
dst-restartcaves 重启地下服务器</br>
dst-mod #content 添加mod，需要重启服务器才能生效</br>
dst-kick #content 踢出饥荒服务器</br>
dst-ban #content 禁止进入饥荒服务器3分钟</br>
dst-msg #content 发送系统消息到饥荒服务器</br>
dst-back #content 回档#content天</br>
dst-list #content 查看当前饥荒服务器玩家列表，content为服务器人数</br>
fd start 开始复读，将所有qq消息推送到饥荒服务器上</br>
fd cancel 取消复读</br>
baidu #content 输出百度搜索联想词</br>
sj start 开始视奸，将饥荒服务器所有消息推送到qq</br>
sj cancel 取消视奸</br>
sh #content 执行Linux服务器命令，不支持vim之类的命令，慎用</br>
ps-aux 查看Linux服务器内存及cpu状态</br>
ps-sar 查看Linux服务器网络状态</br>
@气球仔 复读到q群</br>

四、登录格式约定</br>
login {"serverIp":"服务器ip","serverUserName":"服务器用户名","serverPassword":"服务器密码","cluster":"饥荒存档名称","robotQQ":"机器人qq","chatList":["可使用气球仔的群聊"]}

尾注：</br>
1.气球仔可以部署到docker，也就是可以在Linux中运行，放到云服可24小时运行，具体做法请自行百度或加群问。
