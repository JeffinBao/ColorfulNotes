# 多彩笔记(ColorfulNotes)

[下载(Download)](https://play.google.com/store/apps/details?id=com.jeffinbao.colorfulnotes)

内容(Contents)
--------

-  [中文](#中文)
-  [English](#english-readme)

中文
--------
## 多彩笔记V1.1.0更新日志(2018/12/24)
  - 修复Bug: 重命名笔记本导致笔记消失
  - 修复其他一些笔记格式相关的Bug
  - 如果你一直在用多彩笔记，期待收到你的来信。我的Email地址在“关于 - 联系作者吐槽”中能找到。谢谢大家！

## 多彩笔记V1.0.0上线日志(2018/03/24)
多彩笔记作为我工作之后第一款比较完整的个人作品，终于要上线1.0.0版本了。本篇日志主要由四部分组成:
-  [主要功能设计理念](#主要功能设计理念)
-  [功能列表](#功能列表)
-  [下载渠道](#下载渠道)
-  [致谢](#致谢)
-  [联系作者](#联系作者)
-  [注意点](#注意点)

### 主要功能设计理念
#### 笔记创建时间带时区显示，并且不会随着时区变化而改变
> “如果你在编写一个记录事件类 app（如日记、记账），则应该在存储时间的同时存储时区，并在 UI 上显示事件发生时当地的时间。否则假设你在温哥华夏日早上 8 点写道「太阳刚出来」，多年后回到中国，你可能会看到一篇写于晚上 11 点的「太阳刚出来」。这点大多数 app 都没做好。 ——Twitter. xhacker”



在刚开始做此款笔记应用时偶然看到@NoPingWest的这条微博，当时还不清楚它讲的问题会怎么体现在app里面，随着开发到时间这个功能后，终于明白了: 调用**System.currentTimeMillils()**方法得到的是从1970年1月1日0点0分0秒开始的毫秒总数，它在不同的时区会自动显示当地时间，这样就会造成上面这条微博所说的情况。正确的处理方案是，在存储笔记创建时间时，把时区信息和时间信息一起存储，而不是单纯存储long型的毫秒值。
#### 笔记同步只有单向到Evernote
市面上几款笔记应用也都接入了Evernote SDK，提供了双向同步功能，即既可以同步笔记到Evernote，也可以从Evernote同步对应笔记本的笔记到自己的笔记应用里面。而我认为，作为一款练手应用来说，它的功能过于单一，很难长期成为用户的主力笔记应用，有可能的情况是：用户尝鲜用这类笔记应用记录了几条文字笔记后就很少再用了，这时候，用户需要的是能将这几条为数不多的笔记同步到他的Evernote里，以保留自己当时的想法。这之后，对于用户来说，这类笔记应用也就再没什么用处了，被卸载的命运在所难免。
#### 笔记本功能
给笔记加入笔记本分类功能，很大一部分原因是想稍微增加一些程序的复杂度。另外不同笔记本有不同的颜色(总共有7种颜色，颜色值来自于坚果手机标准版，感谢锤子科技挑选出这么和谐的7彩颜色)，在整个UI展示上让笔记应用没有那么单调的感觉。多彩笔记的名字自然也就是从这么多种颜色中来的，有了这么多颜色以后，这款应用其他色调只有一种白色，字体颜色选择了不同透明度的黑色。这么做的原因是希望用户更多得被多彩的笔记本吸引，进而去分笔记本记一些笔记。
#### 密码功能
市面上的笔记应用大多没有密码功能，而笔记又属于很私人的东西，不知道是大家都没注意到这点还是什么其它原因都没有做密码功能。我个人平时在使用Evernote的时候，是放在Smartisan自带的加密文件夹里。在多彩笔记里面，实现了简单的密码功能，用户可以设置密码、修改密码、选择是否开启密码，如果忘记密码了，还可以通过一开始设置找回密码的问题来重置密码。当然，如果没有设置找回密码问题或者忘了问题答案，那就没办法了，因为该款笔记没有后台，不能通过其它方式找回密码。

### 功能列表
#### 笔记相关
- 新建笔记
- 修改笔记
- 移动笔记到其它笔记本
- 删除笔记
- 笔记创建时间带时区显示，并且不会随着时区变化而改变
- 笔记支持显示创建地点

#### 笔记本相关
- 新建笔记本
- 重命名笔记本
- 删除笔记本

#### 同步功能相关
- 同步所有笔记到Evernote
- 同步单条笔记到Evernote

#### 密码功能相关
- 设置笔记密码
- 修改密码
- 设置找回密码问题
- 设置是否开启密码功能

#### 语言支持
- 中文
- 英文

### 下载渠道
- [Google Play](https://play.google.com/store/apps/details?id=com.jeffinbao.colorfulnotes)
- 锤子应用商店
- 小米应用商店
- 华为应用市场
- 应用宝
- 阿里应用分发渠道

### 致谢
- Afinal
- EventBus
- 友盟
- 印象笔记
- 高德地图

### 联系作者
- [个人主页](http://jeffinbao.github.io/)
- [领英](https://www.linkedin.com/in/jeffinbao/)

### 注意点
一些第三方sdk的key跟secret信息已删掉，如果要运行，请申请相关的key和secret。

English Readme
--------
## ColorfulNotes V1.1.0 Update Log(12/24/2018)
  - Bug fix: notes missing when renaming notebook
  - Fixed other note format bugs
  - If you do use my application for a while and have any thoughts, feel free to drop me an email. My email address can be found under "About -> Contact Developer"

## ColorfulNotes V1.0.0 Log(03/24/2018)
-  [Function List](#function-list)
-  [Where to Download?](#where-to-download)
-  [Thanks to](#thanks-to)
-  [Contact Developer](#contact-developer)
-  [Reminder](#a-small-reminder)

### Function List
#### Note Related
- Create a note
- Modify a note
- Move a note to other notebook
- Delete a note
- Note's creating and last update time with time zone information
- Note's location information will be stored

#### Notebook Related
- Create a notebook
- Rename a notebook
- Delete a notebook

#### Sync Related
- Sync all notes to Evernote
- Sync a single note to Evernote

#### Password Related
- Set a password
- Change a password
- Set password reset question
- Enable password function

#### Language Support
- Simple Chinese
- English

### Where to Download?
- [Google Play](https://play.google.com/store/apps/details?id=com.jeffinbao.colorfulnotes)
- [Smartisan App Store]()
- [Xiaomi App Store]()
- [Huawei App Store]()
- [Tencent App Store]()
- [Alibaba App Store]()

### Thanks to
- Afinal
- EventBus
- UMeng SDK
- Evernote SDK
- AMap SDK

### Contact Developer
- [Linkedin](https://www.linkedin.com/in/jeffinbao/)

### a Small Reminder

I have excluded third party SDK key and screct, if you want to run the project, please apply for your own Evernote, UMeng and AMap developer keys.