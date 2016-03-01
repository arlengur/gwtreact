--remove malerts with relations originator -> task and source -> policy . #BUG 6206
DELETE FROM malertreport
 WHERE alert_id in (SELECT alert.id
  FROM malert as alert, magenttask as task where alert.originator_id=task.id);
DELETE FROM malertupdate
 WHERE alert_id in (SELECT alert.id
  FROM malert as alert, magenttask as task where alert.originator_id=task.id);
DELETE FROM malertreport
 WHERE alert_id in (SELECT alert.id
  FROM malert as alert, mpolicy as policy where alert.source_id=policy.id);
DELETE FROM malertupdate
 WHERE alert_id in (SELECT alert.id
  FROM malert as alert, mpolicy as policy where alert.source_id=policy.id);

DELETE
  FROM malert where malert.id in (SELECT alert.id
  FROM malert as alert, mpolicy as policy where alert.source_id=policy.id);
DELETE
  FROM malert where malert.id in (SELECT alert.id
  FROM malert as alert, magenttask as task where alert.originator_id=task.id);
