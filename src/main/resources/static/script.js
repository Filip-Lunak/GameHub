// Zkontroluje přihlášení při načtení každé stránky
async function checkAuth() {
    const response = await fetch("/api/auth/me");

    const navRight = document.querySelector(".nav-right");
    const mobileLogin = document.querySelector(".mobile-login");

    if (response.ok) {
        navRight.innerHTML = `
            <a href="/kosik.html">🛒</a>
            <a href="#" id="logout-btn">Odhlásit se</a>
        `;
        if (mobileLogin) {
            mobileLogin.innerHTML = `<a href="#" id="logout-btn-mobile">Odhlásit se</a>`;
        }
        document.getElementById("logout-btn")?.addEventListener("click", logout);
        document.getElementById("logout-btn-mobile")?.addEventListener("click", logout);
    } else {
        navRight.innerHTML = `<a href="/prihlaseni.html">Přihlásit se</a>`;
        if (mobileLogin) {
            mobileLogin.innerHTML = `<a href="/prihlaseni.html" class="mobile-login">Přihlásit se</a>`;
        }
    }
}

// Registrace
async function register(e) {
    e.preventDefault();
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const passwordConfirm = document.getElementById("password-confirm").value;

    if (password !== passwordConfirm) {
        alert("Hesla se neshodují");
        return;
    }

    const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password })
    });

    const data = await response.json();

    if (response.ok) {
        alert("Registrace úspěšná, můžeš se přihlásit");
        window.location.href = "/prihlaseni.html";
    } else {
        alert(data.error);
    }
}

// Přihlášení
async function login(e) {
    e.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    const data = await response.json();

    if (response.ok) {
        window.location.href = "/obchod.html";
    } else {
        alert(data.error);
    }
}

// Odhlášení
async function logout(e) {
    e.preventDefault();
    await fetch("/api/auth/logout", { method: "POST" });
    window.location.href = "/prihlaseni.html";
}

// Načtení knihovny
async function loadLibrary() {
    const response = await fetch("/api/page/library");
    if (response.status === 401) {
        window.location.href = "/prihlaseni.html";
    } else {
        document.body.style.visibility = "visible";
    }
}

// Načtení podpory
async function loadSupport() {
    const response = await fetch("/api/page/support");
    if (response.status === 401) {
        window.location.href = "/prihlaseni.html";
    } else {
        document.body.style.visibility = "visible";
    }
}

// Přidání do košíku
async function addToCart(gameId) {
    const response = await fetch(`/api/cart/add/${gameId}`, { method: "POST" });
    const data = await response.json();

    if (response.ok) {
        alert("Hra přidána do košíku");
    } else if (response.status === 401) {
        window.location.href = "/prihlaseni.html";
    } else {
        alert(data.error);
    }
}

// Načtení a zobrazení košíku
async function loadCart() {
    const response = await fetch("/api/cart");

    if (response.status === 401) {
        window.location.href = "/prihlaseni.html";
        return;
    }

    document.body.style.visibility = "visible";

    const items = await response.json();
    const cartItemsEl = document.querySelector(".cart-items");
    const summaryEl = document.querySelector(".cart-summary");

    if (!cartItemsEl) return;

    if (items.length === 0) {
        cartItemsEl.innerHTML = `<p style="color: white; font-size: 1.5rem;">Košík je prázdný</p>`;
        summaryEl.style.display = "none";
        return;
    }

    // Vykreslení položky
    cartItemsEl.innerHTML = items.map(item => `
        <div class="cart-item">
            <div class="cart-item-info">
                <span class="cart-item-title">${item.gameName}</span>
            </div>
            <div class="cart-item-right">
                <span class="cart-item-price">${item.gamePrice} Kč</span>
                <button class="cart-remove-button" onclick="removeFromCart(${item.idGame})">Odebrat</button>
            </div>
        </div>
    `).join("");

    // Spočtení celkové ceny
    const total = items.reduce((sum, item) => sum + item.gamePrice, 0);

    // Vykreslení shrnutí
    document.querySelector(".cart-summary").innerHTML = `
        <h2 class="cart-summary-title">Shrnutí objednávky</h2>
        ${items.map(item => `
            <div class="cart-summary-row">
                <span>${item.gameName}</span>
                <span>${item.gamePrice} Kč</span>
            </div>
        `).join("")}
        <div class="cart-summary-divider"></div>
        <div class="cart-summary-row cart-summary-total">
            <span>Celkem</span>
            <span>${total} Kč</span>
        </div>
        <button class="cart-checkout-button">Zaplatit</button>
    `;
}

// Odebrání z košíku
async function removeFromCart(gameId) {
    const response = await fetch(`/api/cart/remove/${gameId}`, { method: "DELETE" });

    if (response.ok) {
        loadCart(); // Znovu načti košík
    } else {
        alert("Chyba při odebírání hry");
    }
}

// Po načtení stránky zkontroluje přihlášení a spustí funkce podle aktuální stránky
document.addEventListener("DOMContentLoaded", () => {
    const path = window.location.pathname;

    checkAuth();

    if (path.includes("registrace")) {
        document.querySelector(".login-form")?.addEventListener("submit", register);
    }
    if (path.includes("prihlaseni")) {
        document.querySelector(".login-form")?.addEventListener("submit", login);
    }
    if (path.includes("kosik")) {
        loadCart();
    }
    if (path.includes("knihovna")) {
        loadLibrary();
    }
    if (path.includes("podpora")) {
        loadSupport();
    }
});