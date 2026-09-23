select cf.owner_repo,cf.fileName,pr.url,pr.agent
from pullrequests pr 
inner join repositories r on pr.owner_repo = r.owner_repo
inner join changedFiles cf on pr.owner_repo = cf.owner_repo and pr.pr_number = cf.pr_number
where r.isfork = 0
and r.stars >= 10
and r.language = 'Java'
and cf.typeChange = 'Added'
and cf.fileName LIKE '%.java';
