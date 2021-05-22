**Intent就是以当前组件为起点（携带数据），经过层层筛选到达目的地组件。**

有ComponentName的精确筛选为显式Intent，其他通过Action、data、category、extra、flag一层层非精确筛选的称为隐式Intent。

`startActivity(ForResult)`

`startService()`

`send(Ordered)Broadcast()`

# 显式Intent

设置**ComponentName**的Intent。`setComponent()`、`setClass()`、`setClassName()`、构造函数`Intent(this,xx.class)`

```kotlin
startActivity(Intent().apply {
	component= ComponentName(packageName,fullClassName)
	setClass(this@MainActivity/*表示不是这个扩展函数apply的this*/,ServiceActivity::class.java)
	setClassName(packageName,fullClassName)
  //最终都是mComponent = new ComponentName(packageName, className);创建ComponentName
})
```

# 隐式Intent

使用隐式 Intent 时，Android 系统通过将 Intent 的内容与在设备上其他应用的[清单文件](https://developer.android.com/guide/topics/manifest/manifest-intro?hl=zh-cn)中声明的 *Intent 过滤器*进行比较，从而找到要启动的相应组件。

如果 Intent 与 Intent 过滤器匹配，则系统将启动该组件，并向其传递 `Intent` 对象。如果多个 Intent 过滤器兼容，则系统会弹出应用选择器。

Intent就是以当前组件为起点（携带数据），经过**层层筛选到达目的地组件**。

| 筛选条件          | 起点组件代码设置                             | 目的地组件xml设置        |
| ----------------- | -------------------------------------------- | ------------------------ |
| **ComponentName** | `setComponent`、`setClass(Name)`、构造       | 显式                     |
| **action**        | `setAction`、构造                            | `<action/>`              |
| **data/type**     | 各自`setData(Uri)\Type` 同时`setDataAndType` | `<action uri mimeType/>` |
| **category**      | `addCategory`                                | `<action category/>`     |
| **extra**         | `putExtra(包.k,v)`、`putExtras(Bundle)`      |                          |
| **Flag**          | `setFlags`                                   |                          |

**ComponentName指定组件名筛选**。由包名和全类名构成，没有该项即为隐试Intent。

**action指定操作筛选**。如ACTION_VIEW查看的操作；ACTION_SEND发送分享的操作。`ACTION_MAIN` 作为app入口的操作(xml定义)。自定义action供app内部或外部使用时，必须把包名作为其前缀。

**data指定Uri；type指定MIME媒体类型筛选**。即根据资源路径和接收资源类型来筛选。data/type类型也常由action决定。

```kotlin
Intent(Intent.ACTION_DIAL, Uri.parse("tel:5551234"))

Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_EMAIL, arrayOf("jan@example.com")) // recipients
}
```

**category指定类别描述筛选**。如：

1. `CATEGORY_DEFAULT` 的activity才可以被隐式启动；
2. `CATEGORY_BROWSABLE`Activity可通过网络浏览器启动；
3. `CATEGORY_LAUNCHER`的Activity是Task的初始Activity，在系统的launcher中列出(没指定icon，则用外层application的icon)。

**Extra筛选后到达目的地携带的数据**。键值对或bundle。如使用 `ACTION_SEND` 发送邮件时，可用 `EXTRA_EMAIL`指定收件人。自定义的Extra键必须把包名作为其前缀。

**Flag是Intent元数据**：该Intent如何启动目标组件。如FLAG_NEW_TASK

# 清单文件`<intent-filter>`

1.组件的 [`exported`](https://developer.android.com/guide/topics/manifest/activity-element?hl=zh-cn#exported) 属性设置是否能被外部app打开。

2.广播接收器的过滤器，可以通过调用 `registerReceiver()` 动态注册。`unregisterReceiver()` 注销。

3.当系统进程收到隐式 Intent 来启动组件时，把Intent与`<intent-filter>`内的 `<action>`、`<data>`、`<category>`进行匹配，筛选出符合的组件。

## `<data>`

可指定 Uri 结构和MIME 媒体类型

- Uri 的每个部分都是一个单独的属性：`scheme`、`host`、`port`  `path` 组成 `<scheme>://<host>:<port>/<path>`每个属性均为可选。
  从上级到下级来匹配。如仅指定`scheme`，则所有同`scheme`的匹配成功。
- 隐式Intent的data里仅设置了Uri或Mime，则`<intent-filter>`内仅设置了那一项，且相匹配的才能匹配成功。同时设置的要`<intent-filter>`内要同时设置切相匹配才成功。
- 若`<intent-filter>`内设置了`mimeType`，但没设置`scheme`,则默认采用 `content:` 和 `file:` 的scheme，即隐式Intent的data里有 `content:` 或 `file:` 的Uri才能匹配成功。

```kotlin
startActivity(Intent().apply {
    action = Intent.ACTION_VIEW
    data= Uri.parse("xl://goods:8888/goodsDetail?goodsId=10011002")
})
```

```xml
<data
    android:scheme="xl"
    android:host="goods"
    android:port="8888"
    android:path="/goodsDetail"
      
    android:pathPattern="xxx"
		android:pathPrefix="xxx"
    android:mimeType="xxx"
      />
匹配规则
```



# 其他

`PackageManager` 提供一整套

1.  `query...()`方法来返回所有能够接受特定 Intent 的组件。

2.  `resolve...()` 方法来确定响应 Intent 的最佳组件。

   

   例如，`queryIntentActivities()` 将返回能够执行作为参数传递的 Intent 中列出的所有 Activity，而 `queryIntentServices()` 则可返回类似的一系列服务。这两种方法均不会激活组件；而只是列出能够响应的组件。对于广播接收器，有一种类似的方法：`queryBroadcastReceivers()`。

## PendingIntent

`PendingIntent.getActivity/Service/Broadcast()`，声明用于启动组件的 `Intent`。

从**获取一个PendingIntent，在某个时机去启动组件**（如sendBroadcast、startService、startActivity）。即便发起端的进程被杀死，pendingIntent在处理断的进程照样执行。

[![image001](http://static.oschina.net/uploads/img/201401/30173558_QKN4.png)](http://static.oschina.net/uploads/img/201401/30173558_ULPV.png)

发起端A进程，从AMS所处的系统进程获取一个PendingIntent，然后将PendingIntent传递(binder机制)给B进程，B进程在某个时机，回调PendingIntent的send()动作，启动组件并传送Intent。

从**系统**获取一个PendingIntent，**另一个进程**在某个时机去启动组件。

- AlarmManager某个时机打开闹钟
- NotificationManager某个时机点击通知打开Activity
- 桌面微件Widget

```java
private void sendSimplestNotificationWithAction() {
   //获取PendingIntent
   Intent mainIntent = new Intent(this, MainActivity.class);
   PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);//flag
   //创建 Notification.Builder 对象
   NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
           .setSmallIcon(R.mipmap.ic_launcher)
           .setAutoCancel(true)//点击通知后自动清除
           .setContentIntent(mainPendingIntent);//点击后执行该intent
   mNotifyManager.notify(tag/*tag可选*/,3/*id*/, builder.build());//notify发送通知。
  //取消时，cancel根据id取消。若同时设置了tag必须用id和tag同时取消。
}
```

## 启动外部app组件的方式

a.显式：通过`setComponent()`、`setClass()`、`setClassName()`传入包名和目标activity全类名来启动。内部是通过构建**ComponentName**来确定要启动的组件。

（service只能显式的启动，可以在xml定义，但不可以在xml中定义`<intent-filter>`来隐式启动。）

b.隐式：

1. 通过自定义action
   
   ```kotlin
   val intent= Intent("com.as.interview.app_test")//自定义的action必须以报名开头
         .apply {
         `package`=packageName//action相同时，是否按报名过滤。否则会弹出应用选择器
   }
   if (intent.resolveActivity(packageManager) != null) {
     	//决定出响应该Intent的最佳组件返回一个组件的ComponentName
       /*验证至少有一个能接收该intent的activity*/
    startActivity(intent)
   }
   ```
   
   ```xml
   <activity
       android:name=".TargetActivity2"
       android:enabled="true"
       android:exported="true">
       <intent-filter>
           <action android:name="com.as.interview.app_test" />
           <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
   </activity>
   ```
   
2. 通过Uri启动。定义intent.data的URI来与Intent过滤器的data的URI进行匹配

   ```kotlin
     startActivity(Intent().apply {
      action = Intent.ACTION_VIEW
      data= Uri.parse("xl://goods:8888/goodsDetail?goodsId=10011002")
     })
   ```

   ```xml
   <activity
       android:name=".TargetActivity"
       android:exported="true"
       android:label="Target1">
       <intent-filter>
           <action android:name="android.intent.action.VIEW" />
           <category android:name="android.intent.category.DEFAULT" />
           <category android:name="android.intent.category.BROWSABLE" />
           <data
               android:host="goods"
               android:path="/goodsDetail"
               android:port="8888"
               android:scheme="xl" />
       </intent-filter>
   </activity>
   ```

3. 根据包名查询出该App的启动Activity，然后启动

   ```kotlin
   startActivity(packageManager.getLaunchIntentForPackage(packageName))
   ```



