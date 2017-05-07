参考方法:
直接 git clone https://github.com/wangkkai/wangkkai.github.io.git
在分支source下提交(用来保存原文件)
日常修改，直接在本地的仓库内修改 然后git add,git commit -m "",git push origin source提交到github。然后 hexo g ,hexo d部署发布

若重装系统后，可以clone后，执行npm install hexo、npm install、npm install hexo-deployer-git

note:
	很奇怪的是，之前的_config.yml是要求有空格的，但是在本例中却不能有空格。可能是跟版本有关系吧。
deploy:
  type:git
  repository:"https://github.com/wangkkai/wangkkai.github.io.git"
  branch:master