�ο�����:
ֱ�� git clone https://github.com/wangkkai/wangkkai.github.io.git
�ڷ�֧source���ύ(��������ԭ�ļ�)
�ճ��޸ģ�ֱ���ڱ��صĲֿ����޸� Ȼ��git add,git commit -m "",git push origin source�ύ��github��Ȼ�� hexo g ,hexo d���𷢲�

����װϵͳ�󣬿���clone��Ȼ��cd���ļ��У�ִ��npm install hexo��npm install��npm install hexo-deployer-git --save��ʧ�ܾ�ɾ��nodemodule�ļ���֮������ִ���������
���д����ɾ��.deploy_git�ļ��� Ȼ��hexo , git config --global core.autocrlf false, hexo clean,hexo g



deploy:
  type: git
  repository: https://github.com/wangkkai/wangkkai.github.io.git
  branch: master
