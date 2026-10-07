# LakshanMart - Public Online Access & Tunneling Guide

This guide explains how to expose your local LakshanMart Tomcat instance to a secure public HTTPS URL accessible to anyone on the internet.

---

## ⚡ Option 1: Automated Tunnel Script (Recommended)

Double-click `tunnel.bat` or execute in PowerShell:

```powershell
.\tunnel.ps1
```

The script automatically detects whether `ngrok` or `ssh` (localhost.run) is available on your machine and generates a public HTTPS link.

---

## 🌐 Option 2: Using ngrok (Installed on your machine)

`ngrok` is already installed on your system.

### Step 1: Connect your ngrok Authtoken (one-time setup)
If you haven't added your free token yet, get it from [dashboard.ngrok.com](https://dashboard.ngrok.com) and run:
```powershell
ngrok config add-authtoken <YOUR_NGROK_AUTHTOKEN>
```

### Step 2: Start the tunnel
```powershell
ngrok http 8080
```

### Step 3: Access your store
ngrok will display a forwarding URL like:
```
Forwarding                    https://a1b2-c3d4.ngrok-free.app -> http://localhost:8080
```
Open:
```
https://a1b2-c3d4.ngrok-free.app/LakshanMart/
```
The application will automatically load the full Amazon-style marketplace landing page!

---

## 🚀 Option 3: Zero-Setup SSH Tunnel (No Registration or Token Needed)

Because Windows OpenSSH is already installed on your system, you can expose your application instantly without creating any account or setting up API keys:

```powershell
ssh -R 80:localhost:8080 nokey@localhost.run
```

Once connected, terminal outputs:
```
LakshanMart is live at: https://<random-subdomain>.lhr.life
```
Open in any browser:
```
https://<random-subdomain>.lhr.life/LakshanMart/
```

---

## 🔁 Option 4: Pinggy (Alternative Zero-Setup SSH)

```powershell
ssh -p 443 -R0:localhost:8080 a.pinggy.io
```

---

## 🛍️ Verifying the Public URL

Once the tunnel is active, test the complete end-to-end shopping experience over the public internet:
1. **Storefront & Catalog**: `https://<public-url>/LakshanMart/` or `/LakshanMart/home`
2. **Category Filters**: Electronics, Fashion, Home & Kitchen, Books, Sports & Fitness
3. **Product View**: Specifications table, "Add to Cart", and instant "Buy Now"
4. **Authentication**: Register new user or sign in with seed accounts:
   - Admin: `admin@lakshanmart.com` / `Admin@123`
   - Buyer: `bob@lakshanmart.com` / `Admin@123`
   - Seller: `alice@lakshanmart.com` / `Admin@123`
5. **Cart & Checkout**: Persistent cart, simulated UPI/Card/COD, and order confirmation
6. **Account & Orders**: `https://<public-url>/LakshanMart/profile` and `/orders`
7. **Admin Dashboard**: `https://<public-url>/LakshanMart/admin`
