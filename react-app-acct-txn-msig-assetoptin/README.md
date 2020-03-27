# Title: Simple Algo React App for Algorand Accounts and Transactions

## Overview
Simple Algo is a React application that makes use of several Algorand features. Including [account creation](https://developer.algorand.org/docs/features/transactions/#payment-transaction), [account recovery / importing](https://developer.algorand.org/docs/features/transactions/#payment-transaction), [pay transaction](https://developer.algorand.org/docs/features/transactions/#payment-transaction), [multi-sig transactions](https://developer.algorand.org/docs/features/transactions/#payment-transaction) and [ASA opt-in](https://developer.algorand.org/docs/features/asa/#revoking-an-asset) transactions.

## Motivation
React is the most popular front-end library for web development- the motivation to create this app was to lower the barrier to entry for web and full-stack developers to the Algorand blockchain.

## Requirements
- React v16.13.1
- React Router Dom v5.1.2
- Algosdk v1.5.0
- Bootstrap v4.4.1
- Jquery v3.4.1
- @popperjs/core v2.1.1
- clipboard-copy v3.1.0


## Community Development and Recommendations
This app is a great framework for getting started. It was designed and build out of a virtual hackathon with some additions and tweaks made a long the way. Currently, this app is missing an Asset Manager feature. By this I mean, it is missing a way to create assets, configure assets, transfer, delete and revoke assets. The app is also missing a page that can leverage Algorand's Atomic Transfer feature, specifically grouping, signing and sending transactions to the network.
The port, server and token values are pointing to the [sandbox](https://github.com/algorand/sandbox) instance, you can configure how you would like to talk to the Algorand network in `src/services/algorandsdk.js`