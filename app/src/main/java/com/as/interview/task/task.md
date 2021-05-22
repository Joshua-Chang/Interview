按下RecentTask键时，显示出一个个的Task。我们可以在最近的Task间切换。
当我们在某个app内，不停按返回键，直到Task里最后一个Activity被关闭，Task的生命结束。但作为最近的Task，他在RecentTask里并不会消失，仍保留一个残影等待重新打开app。（RecentTask里看到的task未必是存活的）

不仅activity在Task内可以叠成栈，不同task也可以叠成栈。但task的叠加只在前台；一旦进入后台task就不再叠加，并拆开成不同Task。进入后台的场景有按home回到桌面、按recentTask查看最近Task。

1. standard：Activity与启动它的Task相关。在不同Task中打开同一个Activity，Activity会被创建多个实例，分别放进每一个Task顶，互不干扰。
   从A app中打开B app里的Activity时，该Activity会直接放到A的task顶。对于B的task没影响。

   短信app里点击号码添加联系人，通讯录app的添加联系人Activity被打开，放到短信app所在栈的栈顶，对通讯录app没影响。

2. singleTop：和standard一样，只有会复用Task顶的Activity并`onNewIntent`，而不必创建新的实例。
   从A app中打开B app里的Activity时，只有当A Task顶的Activity的刚好是准备要打开的activity时，才不在栈顶创建该Activity，而是复用之前打开的并`onNewIntent`

> 假设主界面为 MainActivity，显示新闻的界面是 DetailActivity，显然显示任何一条新闻都会使用 DetailActivity，即把新闻内容通过 Intent 传给 DetailActivity 就可以了。 假设你正在看新闻1(即在 DetailActivity)，此时手机收到服务器的推送：收到一条通知(新闻2)，点击通知就会跳转到 DetailActivity 并显示新闻2，当你点击通知时，因为目前栈顶的 Activity 就是 DetailActivity，因此这里就是使用 SingleTop 的地方，即点击通知后以 SingleTop 加载模式打开 DetailActivity 并显示新闻2，因此新闻1的 DetailActivity 就被覆盖掉了。 此后你点击返回键会回到主界面。
>
> OnNewIntent
>
> 针对网易新闻这个案例来看，DetailActivity一般是一个webView,然后 根据上一个页面（新闻列表）传过来的url展示对应的网页，“通知“也是一样的。都是通过intent把url传到DetailActivity。以下一段代码简单的模拟DetailActivity 接收数据的过程。


   standard、singleTop都是直接在原Task上新建或复用，而singleTask、singleInstance是跨Task打开Activity(过渡时原生有应用间切换动画)。

3. singleTask：Activity被其他的Task启动的时候：不会直接放到启动他的Task栈顶，而是在自己的Task的栈顶；同时把自己的Task叠加到启动它的Task上(不同task过渡时原生有应用间切换动画)。连按返回键时，退出完自己的task，再退(过渡动画)启动它的task。

   第一步：**将被启动Activity放到被启动Task顶部**：没有原Task(外部app未启动)则新开一个；有task但其内没有要启动的Activity(要启动的外部Activity所在的app已启动，但该Activity不在栈内，即没被启动过)则将被启动的Activity放到所在Task栈顶；有task且task内有要启动的Activity时推到栈顶，上边的被推出。

   | 没有将被启动的Task(所在app未启动)  | 有将被启动的Task                                  |            |
   | ---------------------------------- | ------------------------------------------------- | ---------- |
   | 被启动的activity放入新的task？     | 将被启动的Activity不在task内(activity未被启动)    | 在task     |
   | 被启动的activity再启动其他Activity | 将被启动的Activity放到Task栈顶 (其他Activity在下) | 把顶部推出 |
   | 要看被启动者具体的启动模式         |                                                   |            |

   第二步：将被启动的task移动到当前task顶。

   被启动的Activity原本的Task里，把Activity推到栈顶并`onNewIntent`刷新数据，**Activity原来上边的会被推出**。原来还没有task，新开一个Task。
   特殊情况：Activity被其他的Task启动后，Activity原task叠加在启动它的task上，此时按下home/recentTask从前台切后台，两个task不再叠加，都出现在recentTask里，在Activity原本的Task栈顶回退不会退到启动他的Task。

   短信app里点击邮箱发送邮件，邮箱app的编写邮件Activity被打开。此时回退：会先回退邮箱app打开的页面，再回退短信app的页面。若先按recentTask则发现两个app都出现在最近Task里，再在邮箱里回退不会到短信里。

   设置`android:allowTaskReparenting="true"` 能回到其原来的父Task，的Activity也有类似效果。Activity被其他的Task启动的时候，会把Activity从原来的Task，移到当前Task栈顶（但不像standard到处创建）。直接回退时不会有切换task动画，切后台再回退也不会。若不回退而是打开原来Task，Activity又回移动回原来Task的额栈顶，再切回启动他的Task时Activity不在里边。

   singleTask：只有一个Task里有这个Activity（不同于standard）**唯一性**。

4. singleInstance：比singleTask更严厉，Task里只有一个Activity，要求**独占性**。当Activity被其他的Task启动的时候，把Activity单独放进一个Task（没Task新建后可复用触发`onNewIntent`），并叠加到启动它的Task上（切换动画）。连续退出也是task切换，若切后台也拆成俩Task。
   Home切后台后。若从桌面进入Activity所在的app，原来task的Activity消失（在同TaskAffinity被隐藏的单Activity的task里。此时在recentTask看不见的Task未必被杀死，可能同TaskAffinity被隐藏）。
   recentTask切后台后。若进入只有一个Activity的task退一次就回到桌面。

   若Activity被其他的Task启动，（没切后台）成为一个单Activity的Task叠加到启动他的Task上，（没切后台）再从单Activity的Task启动另一个Activity，新启动的Activity所在的Task（可以是最初Activity的原Task也无所谓，真是这样原task成了俩task叠在一起），会在单Activity的Task上继续叠加。

5. TaskAffinity：Task相关性。默认情况下一个app只有一个Task在recentTask显示。recentTask根据不同的TaskAffinity列出不同的任务，当多个task有相同TaskAffinity时recentTask显示最近前台展示的一个recent的。

   每个Activity有TaskAffinity`<activity>默认取<application>的，默认取包名`所以默认一个task在recentTask里；
   每个Task也有TaskAffinity取自栈底的Activity的TaskAffinity（第一个启动的Activity的TaskAffinity）。


   若新打开的Activity设置了singleTask，则系统要比较Activity和当前Task的TaskAffinity是否相同，相同则在当前task入栈；不同则去寻找TaskAffinity相同的task入栈，没有则新建一个Task。

   所以在打开一个配置了singleTask的Activity时。若是外部app的，TaskAffinity不同，发送task切换；若是app自己的，TaskAffinity相同，则进入栈顶，前面被推出。若给这个Activity设置一个独立的TaskAffinity，哪怕是在同一个app内也会被拆到另一个task里，若这个独立的TaskAffinity恰好与其他app的一样，这个Activity甚至会被放到别人的app的task里。

`adb shell dumpsys activity`可查看task与activity关系。`getRunningTasks`等方法早被屏蔽

```kotlin
val ams = getSystemService(ActivityManager::class.java)
for (task in ams.appTasks) {
    Log.e(TAG, "${task.taskInfo.numActivities}:[${task.taskInfo.baseActivity?.className}->${task.taskInfo.topActivity?.className}]")
}
```

