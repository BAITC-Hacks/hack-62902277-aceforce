// Optional live JAR check: Node.js 18+, run after mvn package.
const {spawn}=require('node:child_process');
const assert=require('node:assert/strict');
const app=spawn('java',['-jar','target/career-quest-1.0.0.jar','--spring.profiles.active=demo','--server.port=18080'],{stdio:['ignore','pipe','pipe']});
let logs=''; app.stdout.on('data',d=>logs+=d); app.stderr.on('data',d=>logs+=d);
let cookie='';
async function request(path,options={}) {
  const response=await fetch('http://127.0.0.1:18080'+path,{...options,redirect:'manual',headers:{Cookie:cookie,...options.headers}});
  for(const c of response.headers.getSetCookie()) if(c.startsWith('JSESSIONID=')) cookie=c.split(';')[0];
  return response;
}
async function main() {
  let login;
  for(let attempt=0;attempt<100;attempt++) {
    try { login=await request('/login'); if(login.status===200) break; } catch {}
    await new Promise(r=>setTimeout(r,200));
  }
  assert.equal(login?.status,200,'JAR must serve login');
  const html=await login.text();
  const token=html.match(/name="_csrf"[^>]*value="([^"]+)"/)?.[1];
  assert.ok(token,'CSRF hidden field');
  // Bootstrap is transactional and may finish shortly after the HTTP listener starts.
  await new Promise(r=>setTimeout(r,1000));
  let response=await request('/login',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams({username:'hr',password:'CareerDemo123!',_csrf:token})});
  assert.equal(response.status,302);
  assert.ok(!response.headers.get('location').includes('error'));
  response=await request('/api/data/summary');
  assert.equal(response.status,200);
  const summary=await response.json();
  assert.deepEqual(summary,{employees:200,skills:60,roleProfiles:32,events:40,activityRecords:2743});
  console.log('Live dataset:',summary);
  for(const path of ['/?lang=ru','/?lang=kk','/?lang=en','/activities','/achievements','/hr']) {
    const started=Date.now(); const page=await request(path);
    assert.equal(page.status,200,path);
    assert.ok((await page.text()).includes('Career Quest'),path);
    console.log('HTTP 200',path,Date.now()-started+'ms');
  }
  console.log('LIVE SMOKE PASSED');
}
main().catch(e=>{console.error(e);console.error(logs.slice(-5000));process.exitCode=1;}).finally(()=>app.kill('SIGTERM'));
