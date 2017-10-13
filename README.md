# HenCoderPractice

HenCoder 三维旋转动效的实现

![Alt text](/jpg/flipboard.gif)

>这是凯哥在 HenCoder 讲解几何变换章节提到的一个动画栗子，很炫酷，极大地激发了偶的好奇心。正好这个周末手头没啥事儿，研究了一下实现。

* 首先，拆解动效，把gif图片放到手机上，咔咔咔不停地截屏，截出一帧一帧的图片
* 然后，分析这些图片，可以发现，动效分为三部分：
  * 开始动画，一个Y轴旋转45度动效，看过hencoder教程的童鞋应该很轻松实现
  * 中间动画，比较复杂，后面着重分解
  * 结束动画，一个绕X轴旋转45度动效，看过hencoder教程的童鞋应该很轻松实现

重点分析中间动画，一帧一帧观察，基本判断是，先旋转canvas坐标系，再裁切图形，然后使用camera3D旋转，保存camera效果 `camera.applyToCanvas(canvas);`，然后再把canvas坐标系旋转回来。本来想画个图解释一下，奈何时间有限，你先仔细体会一下吧......

实现中间动画，原理上面已经说了
```
		canvas.save();
	    camera.save();
	    canvas.translate(centerX, centerY);
	    canvas.rotate(-degreeZ);
	    camera.rotateY(degreeY);
	    camera.applyToCanvas(canvas);
	    //计算裁切参数时清注意，此时的canvas的坐标系已经移动
	    canvas.clipRect(0, -centerY, centerX, centerY);
	    canvas.rotate(degreeZ);
	    canvas.translate(-centerX, -centerY);
	    camera.restore();
	    canvas.drawBitmap(bitmap, x, y, paint);
	    canvas.restore();
```
划重点，需要注意：

	1、Canvas的几何变换，顺序是倒序
	2、clipRect执行时，canvas的坐标系已经被我们移动了，计算参数时要以移动后的坐标系计算
	3、循环播放动效时，在执行动效前，要重置几个动效相关的参数值
	4、第三个动效执行时，我们的canvas已经旋转了270度，所以此时的旋转<h2>实际上</h2>是绕Y轴旋转，而且旋转角度的正负也是反着的，你看下代码，再仔细体会一下


另外，AnimatorSet在 playSequentially 时，不能循环播放，所以我给set加了一个监听，间接实现了循环，求各位大神，有木有优雅的循环实现方式？

