delimiter $$
create event  if not exists TASK_STATUS_ING
on schedule every 30 minute starts now()
comment '30분 마다 task 의 startdate 가 현재 시간보다 작으면 status 를 ing 로 변경'
do
begin
	update task set status = case when date_format(now(), '%Y%m%d%H%i%s') >= startdate then 'ING' else 'REG' end where status = 'REG';
END$$


drop event WORK_STATUS_ING;

delimiter $$
create event  if not exists WORK_STATUS_ING
on schedule every 30 minute starts now()
comment '30분 마다 work 의 startdate 가 현재 시간보다 작으면 status 를 ing 로 변경'
do
begin
	update work set status = case when date_format(now(), '%Y%m%d%H%i%s') >= startdate then 'ING' else 'ACCEPT' end where status ='ACCEPT';
END$$
