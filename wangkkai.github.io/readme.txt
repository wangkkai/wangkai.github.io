�ο�����:
ֱ�� git clone https://github.com/wangkkai/wangkkai.github.io.git
�ڷ�֧source���ύ(��������ԭ�ļ�)
�ճ��޸ģ�ֱ���ڱ��صĲֿ����޸� Ȼ��git add,git commit -m "",git push origin source�ύ��github��Ȼ�� hexo g ,hexo d���𷢲�

����װϵͳ�󣬿���clone��ִ��npm install hexo��npm install��npm install hexo-deployer-git

note:
	����ֵ��ǣ�֮ǰ��_config.yml��Ҫ���пո�ģ������ڱ�����ȴ�����пո񡣿����Ǹ��汾�й�ϵ�ɡ�
deploy:
  type:git
  repository:"https://github.com/wangkkai/wangkkai.github.io.git"
  branch:master