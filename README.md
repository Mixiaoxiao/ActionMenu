ActionMenu
===============

`ActionMenu` is a lite library to show an action-menubar like the menus(copy, select all, paste) for UILabel in iOS.

一个轻巧易用的菜单库，仅需一行代码，即可显示类似于IOS中UILabel的文字菜单（复制、全选、粘贴）的菜单条。

Demo
-----

![ActionMenu](https://raw.github.com/Mixiaoxiao/ActionMenu/master/Demo/demo.jpg)


* The arrow of `ActionMenu` will be always at `center-horizontal|top` of the anchor.
* `ActionMenu`的指示箭头总是会显示在anchor的上方中间位置。

Usage
-----

#####Show menus for a view 显示view的菜单

```java
    ActionMenu.build(activity, view).addActions(actions...).setListener(actionMenuListener).show();
```

#####Show description for a view 显示view的介绍

```java
    ActionMenu.build(activity, view).addAction(description).show();
```


Notice
--------
`ActionMenu` cannot show correctly when the the width of ActioinItems is larger than the Activity's width.
I may fix this in the near future.

注意：在显示多菜单项的情况下，`ActionMenu`不支持显示整体宽度超过屏幕宽度的菜单条。近期可能会完善。


Proguard
--------
In order to use this library with proguard, you need to add, well..., `nothing` to your `proguard.cfg`.



Developed By
------------

Mixiaoxiao - <xiaochyechye@gmail.com> or <mixiaoxiaogogo@163.com>



License
-----------

    Copyright 2015 Mixiaoxiao

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
