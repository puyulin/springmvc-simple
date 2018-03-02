# springmvc-simple


# 问题处理汇总
##	shiro
###		1.登录后sessionid一直变化、导致获取session为NULL
####		原因：登录成功后未调用sessionDao.create()缓存Session	