import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 300,
    duration: '1m',
};

export default function () {
    const res = http.get('http://host.docker.internal:8080/v3/api-docs');

    check(res, {'status was 200': (r) => r.status === 200});

    sleep(0.5);
}