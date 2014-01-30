<html>
    <body>
        <form action="${pageContext.request.contextPath}/upload" method="POST" enctype="multipart/form-data">
            <input type="file" name="uploadedfile" />
            <input type="submit" value="start async upload with separated thread" />
        </form>
        <form action="${pageContext.request.contextPath}/upload2" method="POST" enctype="multipart/form-data">
            <input type="file" name="uploadedfile" />
            <input type="submit" value="start async upload" />
        </form>
    </body>
</html>