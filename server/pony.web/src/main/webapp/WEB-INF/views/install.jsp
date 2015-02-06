<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">

    <title><spring:message code="install.title" /></title>

    <script src="./js/jquery.js"></script>

    <link rel="icon" type="image/png" href="./img/favicon.png">
    <link href="./css/pony.css" rel="stylesheet">

    <script>
        (function() {

            function InstallationGui() {

                this.libraryFolderContainer = $('#libraryFolderContainer');

                this.lastFolderId = this.libraryFolderContainer.children().length;

                this._refreshLibraryFolderControls();
            }

            InstallationGui.prototype.addLibraryFolder = function() {

                var $item = this.libraryFolderContainer.children().eq(0).clone();

                this.libraryFolderContainer.append($item);

                $item.removeClass('has-error');
                $item.find('.help-block').remove();

                var $itemInput = $item.find('input');

                $itemInput.val('');
                $itemInput.focus();

                this._refreshLibraryFolderControls();

                this.lastFolderId++;
            };

            InstallationGui.prototype.removeLibraryFolder = function(index) {

                this.libraryFolderContainer.children().eq(index).remove();

                this._refreshLibraryFolderControls();
            };

            InstallationGui.prototype._refreshLibraryFolderControls = function() {

                if (this.libraryFolderContainer.children().length > 1) {
                    this.libraryFolderContainer.find('button.remove').removeAttr('disabled');
                } else {
                    this.libraryFolderContainer.find('button.remove').attr('disabled', '');
                }

                var self = this;

                this.libraryFolderContainer.children().each(function(index) {

                    var $folderInput = $(this);

                    $folderInput.find('input').attr('name', 'libraryFolders[' + index + '].path');

                    $folderInput.find('button.add').off('click').click(function() {
                        self.addLibraryFolder();
                    });
                    $folderInput.find('button.remove').off('click').click(function() {
                        self.removeLibraryFolder($folderInput.index());
                    });
                });
            };

            var installationGui = null;

            $(document).ready(function() {
                installationGui = new InstallationGui();
            });

        })();
    </script>

</head>
<body>

<spring:message code="install.namePlaceholder" var="namePlaceholder" />
<spring:message code="install.emailPlaceholder" var="emailPlaceholder"/>
<spring:message code="install.passwordPlaceholder" var="passwordPlaceholder" />
<spring:message code="install.folderPathPlaceholder" var="folderPathPlaceholder" />

<div class="container">
    <div class="jumbotron installationContainer">

        <h1><spring:message code="install.header" /></h1>

        <p><spring:message code="install.description" /></p>

        <form:form role="form" method="post" commandName="installCommand">

            <form:errors cssClass="alert alert-danger" role="alert" element="div" />

            <spring:bind path="installCommand.libraryFolders[*">
                <div class="form-group">
                    <div class="${status.error ? 'has-error' : ''}">
                        <label class="control-label"><spring:message code="install.libraryFolders" /></label>
                    </div>
                    <div id="libraryFolderContainer">
                        <c:forEach items="${installCommand.libraryFolders}" var="folder" varStatus="loopStatus">
                            <spring:bind path="libraryFolders[${loopStatus.index}].path">
                                <div class="${status.error ? 'has-error' : ''}">
                                    <div class="input-group">
                                        <form:input path="libraryFolders[${loopStatus.index}].path" type="text" class="form-control" placeholder="${folderPathPlaceholder}" />
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-default add">
                                                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                            </button>
                                            <button type="button" class="btn btn-default remove">
                                                <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
                                            </button>
                                        </span>
                                    </div>
                                    <form:errors path="libraryFolders[${loopStatus.index}].path" cssClass="help-block" />
                                </div>
                            </spring:bind>
                        </c:forEach>
                    </div>
                </div>
            </spring:bind>
            <spring:bind path="userName">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label class="control-label" for="userName"><spring:message code="install.name" /></label>
                    <form:input id="userName" path="userName" type="text" class="form-control" placeholder="${namePlaceholder}" />
                    <form:errors path="userName" cssClass="help-block" />
                </div>
            </spring:bind>
            <spring:bind path="userEmail">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label class="control-label" for="userEmail"><spring:message code="install.email" /></label>
                    <form:input id="userEmail" path="userEmail" type="text" class="form-control" placeholder="${emailPlaceholder}" />
                    <form:errors path="userEmail" cssClass="help-block" />
                </div>
            </spring:bind>
            <spring:bind path="userPassword">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label class="control-label" for="userPassword"><spring:message code="install.password" /></label>
                    <form:input id="userPassword" path="userPassword" type="password" class="form-control" placeholder="${passwordPlaceholder}" />
                    <form:errors path="userPassword" cssClass="help-block" />
                </div>
            </spring:bind>

            <button type="submit" class="btn btn-primary btn-lg"><spring:message code="install.button" /></button>

        </form:form>

    </div>
</div>

</body>
</html>