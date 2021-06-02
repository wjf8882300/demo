# bill

财务管理系统

#### 问题：
> 1、请假跨节日，excel没有自动排除，会作为请假算进去，需要排除。Y
> 2、工资条可以导出excel表。
> 3、代账公司可看报表，及其修改个税。 Y
> 4、避税部分报表，及其导出。 Y
> 5、全量工资表 Y
> 6、公司名义分开 Y
> 7、发票管理系统 Y
> 8、服务器维护 Y
> 9、迟到规则 Y
  1）迟到30分钟内，扣30元；
  2）迟到30至60分钟，扣款100元；
  3）迟到60至90分钟，以事假2小时计算；
  4）迟到90分钟以上，以事假半天计算；
  5）迟到超过120分钟的，以事假1天计算；
  迟到券抵扣规则：
  1）迟到60分钟以内的，按照打卡时间倒序排序；（优先抵扣打卡时间最晚的）
  2）迟到超过60分钟，迟到时间减去60分钟，得到真实迟到时间，再用迟到规则计算。
> 10、年假自动计算 Y
> 11、公司支出和收入
  1） 收入来源 
  2）支出目标 工资、办公产品、奖金、租金、绿植、交税、其他
> 12、操作日志 Y
> 13、登陆日志 Y 
> 14、发票按照名称查询 Y
> 15、钉钉通知
  1）生成工资条之后发送（群通知）Y
     计薪天数、月份
  2）定时发送
     a.上传月度汇总后统计上个月考勤情况并发送（个人通知）Y
       当月缺勤次数、迟到次数、剩余迟到券、补贴金额（调休小时、年假小时、事假小时、病假小时有则显示）
     b.节假日通知（群通知）下午五点执行 Y
       检查第二天是否是周末且要上班，是则通知
       检查第二天是否是节假日（非周末），连续的需要判断，是则通知
     c.每月10号（非工作日顺延）给人事发通知，提醒工资结算;
       尤翌发薪日为20日，请尽早结算x月工资
     d.4月、7月、10月、1月的20号通知给人事发通知，提醒房租对外付款
       x月-x月交房租了
     e.工资条生成后通知财务核对个税、上传社保公积金个税凭证
     f.每月5号（非工作日顺延）给财务发通知，提醒开65000的发票
     g.4月、7月、10月、1月的15号给财务发通知，提醒开63000的发票
> 16、考勤记录新增筛选条件迟到和缺勤、迟到、缺勤、请假、激励和感恩、激励、感恩（群通知） Y