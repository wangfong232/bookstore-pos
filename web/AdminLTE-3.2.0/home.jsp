<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bookstore - Chào mừng</title>

    <!-- Google Font -->
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700;800&display=swap">
    <!-- FontAwesome -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            overflow-x: hidden;
            background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
            color: #fff;
        }

        /* Animated background particles */
        .bg-particles {
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            z-index: 0;
            overflow: hidden;
        }
        .bg-particles span {
            position: absolute;
            display: block;
            width: 20px; height: 20px;
            background: rgba(255,255,255,0.05);
            animation: floatUp 25s infinite linear;
            bottom: -150px;
            border-radius: 50%;
        }
        .bg-particles span:nth-child(1)  { left: 10%; width:40px; height:40px; animation-delay:0s; animation-duration:20s; }
        .bg-particles span:nth-child(2)  { left: 25%; width:20px; height:20px; animation-delay:2s; animation-duration:25s; }
        .bg-particles span:nth-child(3)  { left: 40%; width:60px; height:60px; animation-delay:4s; animation-duration:22s; }
        .bg-particles span:nth-child(4)  { left: 55%; width:30px; height:30px; animation-delay:0s; animation-duration:18s; }
        .bg-particles span:nth-child(5)  { left: 70%; width:15px; height:15px; animation-delay:3s; animation-duration:30s; }
        .bg-particles span:nth-child(6)  { left: 85%; width:50px; height:50px; animation-delay:7s; animation-duration:24s; }
        .bg-particles span:nth-child(7)  { left: 15%; width:25px; height:25px; animation-delay:5s; animation-duration:28s; }
        .bg-particles span:nth-child(8)  { left: 50%; width:35px; height:35px; animation-delay:8s; animation-duration:19s; }
        .bg-particles span:nth-child(9)  { left: 65%; width:45px; height:45px; animation-delay:1s; animation-duration:26s; }
        .bg-particles span:nth-child(10) { left: 90%; width:18px; height:18px; animation-delay:6s; animation-duration:21s; }

        @keyframes floatUp {
            0%   { transform: translateY(0) rotate(0deg); opacity: 1; }
            100% { transform: translateY(-1200px) rotate(720deg); opacity: 0; }
        }

        /* Main container */
        .hero-container {
            position: relative;
            z-index: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 40px 20px;
            text-align: center;
        }

        /* Logo icon */
        .logo-icon {
            width: 100px; height: 100px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            border-radius: 24px;
            display: flex; align-items: center; justify-content: center;
            font-size: 48px;
            margin-bottom: 30px;
            box-shadow: 0 20px 60px rgba(102, 126, 234, 0.4);
            animation: pulse 3s ease-in-out infinite;
        }

        @keyframes pulse {
            0%, 100% { transform: scale(1); box-shadow: 0 20px 60px rgba(102,126,234,0.4); }
            50%      { transform: scale(1.05); box-shadow: 0 25px 70px rgba(102,126,234,0.6); }
        }

        .hero-title {
            font-size: 3.5rem;
            font-weight: 800;
            margin-bottom: 10px;
            background: linear-gradient(135deg, #fff 0%, #c4b5fd 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: -1px;
        }

        .hero-subtitle {
            font-size: 1.25rem;
            font-weight: 300;
            color: rgba(255,255,255,0.7);
            margin-bottom: 50px;
            max-width: 600px;
            line-height: 1.7;
        }

        /* Buttons */
        .btn-group-hero {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            justify-content: center;
        }

        .btn-hero {
            display: inline-flex;
            align-items: center;
            gap: 12px;
            padding: 16px 40px;
            font-size: 1.05rem;
            font-weight: 600;
            font-family: 'Poppins', sans-serif;
            border: none;
            border-radius: 50px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
            letter-spacing: 0.5px;
        }

        .btn-hero-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            box-shadow: 0 10px 40px rgba(102,126,234,0.4);
        }
        .btn-hero-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 15px 50px rgba(102,126,234,0.6);
            color: #fff;
            text-decoration: none;
        }

        .btn-hero-outline {
            background: transparent;
            color: #fff;
            border: 2px solid rgba(255,255,255,0.3);
            backdrop-filter: blur(10px);
        }
        .btn-hero-outline:hover {
            transform: translateY(-3px);
            border-color: rgba(255,255,255,0.7);
            background: rgba(255,255,255,0.1);
            color: #fff;
            text-decoration: none;
        }

        /* Features */
        .features {
            display: flex;
            gap: 40px;
            margin-top: 80px;
            flex-wrap: wrap;
            justify-content: center;
        }

        .feature-card {
            background: rgba(255,255,255,0.05);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 20px;
            padding: 30px 25px;
            width: 220px;
            text-align: center;
            transition: transform 0.3s, box-shadow 0.3s;
        }
        .feature-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
        }

        .feature-icon {
            font-size: 2rem;
            margin-bottom: 15px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .feature-title {
            font-weight: 600;
            font-size: 1rem;
            margin-bottom: 8px;
        }

        .feature-desc {
            font-size: 0.85rem;
            color: rgba(255,255,255,0.5);
            line-height: 1.5;
        }

        @media (max-width: 768px) {
            .hero-title { font-size: 2.2rem; }
            .hero-subtitle { font-size: 1rem; }
            .btn-hero { padding: 14px 30px; font-size: 0.95rem; }
            .features { gap: 20px; }
            .feature-card { width: 160px; padding: 20px 15px; }
        }
    </style>
</head>
<body>

    <!-- Floating background particles -->
    <div class="bg-particles">
        <span></span><span></span><span></span><span></span><span></span>
        <span></span><span></span><span></span><span></span><span></span>
    </div>

    <div class="hero-container">
        <!-- Logo -->
        <div class="logo-icon">
            <i class="fas fa-book-open"></i>
        </div>

        <!-- Title -->
        <h1 class="hero-title">Bookstore</h1>
        <p class="hero-subtitle">
            Khám phá bộ sưu tập sách phong phú với hàng ngàn đầu sách từ nhiều thể loại.
            Tìm kiếm, lọc và chọn cho mình những cuốn sách yêu thích.
        </p>

        <!-- CTA Buttons -->
        <div class="btn-group-hero">
            <a href="${pageContext.request.contextPath}/products" class="btn-hero btn-hero-primary" id="btn-view-products">
                <i class="fas fa-shopping-bag"></i>
                Xem sản phẩm
            </a>
            <a href="${pageContext.request.contextPath}/login" class="btn-hero btn-hero-outline" id="btn-login">
                <i class="fas fa-sign-in-alt"></i>
                Đăng nhập
            </a>
        </div>

        <!-- Feature cards -->
        <div class="features">
            <div class="feature-card">
                <div class="feature-icon"><i class="fas fa-book"></i></div>
                <div class="feature-title">Đa dạng thể loại</div>
                <div class="feature-desc">Hàng ngàn đầu sách từ nhiều danh mục khác nhau</div>
            </div>
            <div class="feature-card">
                <div class="feature-icon"><i class="fas fa-filter"></i></div>
                <div class="feature-title">Lọc thông minh</div>
                <div class="feature-desc">Tìm kiếm theo thương hiệu, danh mục dễ dàng</div>
            </div>
            <div class="feature-card">
                <div class="feature-icon"><i class="fas fa-tags"></i></div>
                <div class="feature-title">Giá tốt nhất</div>
                <div class="feature-desc">Cập nhật giá và khuyến mãi liên tục</div>
            </div>
        </div>
    </div>

</body>
</html>
