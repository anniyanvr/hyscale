#! /usr/bin/env node

const sgMail = require('@sendgrid/mail');
sgMail.setApiKey(process.env.SENDGRID_API_KEY);

const msg = {
    to: 'niranjan.andhe@hyscale.io',
    from: 'niranjan.andhe@hyscale.io',
    subject: 'Hyscale Tool Build Failed',
    text: 'Hyscale Tool build failed',
    html: '<p>Hyscale Tool Build Failed!</p>',
};

sgMail
    .send(msg)
    .then(() => console.log('Mail sent successfully'))
    .catch(error => console.error(error.toString()));
