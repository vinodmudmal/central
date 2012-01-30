-- --
-- sets up default users
insert into user
values
(1, 'super', 'super@super.com', SHA1('a'), 'f', 'l', 'en_US', 1, now(), 1);

insert into userrole values (1, 1, 11, now(), 1);
insert into lab values (1, 1, 'default lab', 1, 1, now(), 1);
insert into labuser values (1, 1, 1, 6, now(), 1);


-- --
-- workflow options
insert into workflowresourcecategory
(workflowresourcecategoryid, workflowid, resourcecategoryid)
values
(1, 1, 1);

insert into workflowresourcecategorymeta
(workflowresourcecategoryid, k, v, position)
values
(1, 'illuminaH2000.allowableUiField.readlength', '10:10;20:20;', 0);
insert into workflowresourcecategorymeta
(workflowresourcecategoryid, k, v, position)
values
(1, 'illuminaH2000.allowableUiField.readType', 'single:single;pair:pair;', 0);


insert into workflowsoftware
(workflowsoftwareid, workflowid, softwareid)
values
(1, 1, 1);
insert into workflowsoftware
(workflowsoftwareid, workflowid, softwareid)
values
(2, 1, 2);

insert into resource
values
(1, 1, 1, 'illuminia1', 'illuminia #1', 1, now(), null);


-- --
-- make a test job
insert into job
values
(1, 1, 1, 1, 'first job', now(), null, 1, now(), null);

insert into sample
values
(1, 1, 2, 1, 1, 1, 1, null, null, 'dna A', 1, 1, now(), null);

insert into jobsample values (1, 1, 1, now(), 1);

insert into jobsoftware values (1, 1, 1, now(), null);
insert into jobsoftware values (2, 1, 2, now(), null);

insert into state
select 1, taskid, name, 'CREATED', null, now(), null, now(), 1 from task where iname = 'Start Job';
insert into statejob values (1, 1, 1);



-- --
-- another job
-- assumes 1 is chipseqdna
insert into job
values
(2, 1, 1, 1, 'second job', now(), null, 1, now(), null);
insert into sample
values
(2, 3, 3, 1, 1, 1, 1, null, null, 'library B', 1, 1, now(), null);
insert into jobsample values (2, 2, 2, now(), 1);

insert into state
select 100, taskid, name, 'CREATED', null, now(), null, now(), 1 from task where iname = 'Start Job';
insert into statejob values (100, 100, 2);


-- --
-- simulate pi/da/boneshaker approval
update state set status = "APPROVED" where name like '%Appr%';


-- --
-- simulate quote job
update state set status = "QUOTED" where name like '%Quote%';

-- --
-- simulate sample received  (should set isreceived flag and play w/ that
update state set status = "RECEIVED" where name like '%Receiv%' and status != 'FINAL';


-- --
-- simulates library creation (for dna A)
insert into sample
values
(3, 3, 3, 1, 1, 1, 1, 1, now(), 'library A', 1, 1, now(), null);

insert into samplesource
(sampleid, source_sampleid, multiplexindex, lastupduser, lastupdts)
values
(3, 1, 1, 1, now());

insert into jobsample values (4, 1, 3, now(), 1);

update state set status = "MADE" where name like '%Create Library%' and status != 'FINAL';


-- --
-- rereceive library?
update state set status = "RECEIVED" where name like '%Receiv%' and status != 'FINAL';

-- --
-- simulates flowcell creation
insert into sample
values
(14, 5, 1, null, 1, null, 1, 1, now(), 'flowcell A', 1, 1, now(), null);
insert into sample
values
(15, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/1', 1, 1, now(), null),
(16, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/2', 1, 1, now(), null),
(17, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/3', 1, 1, now(), null),
(18, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/4', 1, 1, now(), null),
(19, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/5', 1, 1, now(), null),
(20, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/6', 1, 1, now(), null),
(21, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/7', 1, 1, now(), null),
(22, 4, null, null, 1, null, 1, 1, now(), 'flowcell lane A/8', 1, 1, now(), null);


insert into samplesource
(sampleid, source_sampleid, multiplexindex, lastupduser, lastupdts)
select s1.sampleid, s2.sampleid, s2.sampleid-14, 1, now()
from sample s1, sample s2
where s1.sampleid = 14 and s2.sampleid in (15,16,17,18,19,20,21,22);

        -- 15, sampleWrapTask
insert into state
values
(1010, 15, 'Sample Task (Flowcell)', 'CREATED', 11, now(), null, now(), 1);
insert into statesample
(stateid, sampleid)
values
(1010, 14);

-- --
-- receive flowcell?
update state set status = "RECEIVED" where name like '%Receiv%' and status != 'FINAL';


-- --
-- simulate assign library to flowcell
insert into samplesource
(sampleid, source_sampleid)
values
(15, 3);

insert into samplesource
(sampleid, source_sampleid)
values
(16, 2);

 -- assign both on library side and flowcell side
update state set status = "ASSIGNED" where name like '%Assign Library%' and status != 'FINAL';

--- snap4.sql




-- --
-- simpulte start illumina run

insert into run
values
(1, 1, 1, null, 1, 'Run lola run', 14, now(), null, null, 1, now(), 1);

  -- should source from p.u.
  -- 16 run task wrap
insert into state
values
(2010, 16, 'Run Task', 'CREATED', 1010, now(), null, now(), 1);
insert into statesample
(stateid, sampleid)
values
(2010, 14);
insert into staterun
(stateid, runid)
values
(2010, 1);

update state set status = "PLACED" where name like '%Place Ampli%' and status != 'FINAL';

-- --
-- simulate get run results

-- b:/home/echeng/ed/t> touch file.1.1.3.completed.txt


-- --
-- simulate illumina qc screen ok

update state set status = "APPROVED" where name like '%Qc Appr%';





