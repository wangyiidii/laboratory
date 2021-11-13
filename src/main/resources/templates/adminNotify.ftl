<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta charset="UTF-8">
    <style>
        * {
            font-size: 14px;
        }

        .card {
            border-radius: 4px;
            border: 1px solid #ebeef5;
            background-color: #fff;
            overflow: hidden;
            color: #303133;
            transition: .3s;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
        }

        .card_title {
            padding: 18px 20px;
            border-bottom: 1px solid #ebeef5;
            box-sizing: border-box;
            font-weight: bold;
        }

        .card_body {
            padding: 16px 20px 30px 20px;
        }

        .card_footer {
            text-align: center;
            padding: 18px 20px;
            box-sizing: border-box;
            color: gray;
        }
    </style>
</head>
<body>
<div>
    <div class="card">
        <div class="card_title">${title}</div>
        <div class="card_body">
            ${content}
        </div>
        <div class="card_footer">本邮件由Lab系统发出, 如有疑问请<a href="mailto:3087233411@qq.com?subject=标题&body=内容">联系我</a></div>
    </div>
</div>
<script>

</script>
</body>
</html>
