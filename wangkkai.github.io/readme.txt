参考方法:
直接 git clone https://github.com/wangkkai/wangkkai.github.io.git
在分支source下提交(用来保存原文件)
日常修改，直接在本地的仓库内修改 然后git add,git commit -m "",git push origin source提交到github。然后 hexo g ,hexo d部署发布

若重装系统后，可以clone后，执行npm install hexo、npm install、npm install hexo-deployer-git